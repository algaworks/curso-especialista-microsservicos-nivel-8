package com.algaworks.algashop.ecommerce.application.controller;

import com.algaworks.algashop.ecommerce.application.client.*;
import com.algaworks.algashop.ecommerce.application.model.client.*;
import com.algaworks.algashop.ecommerce.application.model.form.BuyNowCheckoutForm;
import com.algaworks.algashop.ecommerce.application.model.form.CheckoutForm;
import com.algaworks.algashop.ecommerce.application.model.form.PaymentMethod;
import com.algaworks.algashop.ecommerce.application.model.page.AlertMessage;
import com.algaworks.algashop.ecommerce.application.model.page.BuyNowCheckoutPageModel;
import com.algaworks.algashop.ecommerce.application.model.page.CheckoutPageModel;
import com.algaworks.algashop.ecommerce.application.properties.EcommerceProperties;
import com.algaworks.algashop.ecommerce.application.service.ShoppingCartService;
import com.algaworks.algashop.ecommerce.application.util.FullNameParser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class CheckoutController {

	private static final String REDIRECT_SHOPPING_CART = "redirect:/shopping-cart";
	private static final String ALERT_MESSAGE_KEY = "alertMessage";

	private final ShoppingCartService shoppingCartService;
	private final CheckoutClient checkoutClient;
	private final CreditCardClient creditCardClient;
	private final CustomerRestClient customerManagementAPIClient;
	private final ShippingCostClient shippingCostClient;
	private final ProductClient productClient;
	private final EcommerceProperties ecommerceProperties;

	@GetMapping("/checkout")
	public ModelAndView checkout(RedirectAttributes redirectAttributes) {
		ShoppingCartModel shoppingCart;

		try {
			shoppingCart = shoppingCartService.findCurrentShoppingCart();
		} catch (Exception e) {
			log.error("Error loading shopping cart", e);
			return new ModelAndView(REDIRECT_SHOPPING_CART);
		}

		if (shoppingCart.getTotalItems() < 1) {
			return new ModelAndView(REDIRECT_SHOPPING_CART);
		}

		if (shoppingCart.containsUnavailableItems()) {
			return redirectToCartWithUnavailableItemsAlert(redirectAttributes);
		}

		CustomerModel customerModel;

		try {
			customerModel = customerManagementAPIClient.getMyProfile();
		} catch (Exception _) {
			log.error("Error loading customer profile", new Exception());
			return checkout(new CheckoutForm());
		}

		return checkout(CheckoutForm.of(customerModel));
	}

	private ModelAndView checkout(CheckoutForm checkoutForm) {
		return checkout(checkoutForm, null);
	}

	private ModelAndView checkout(CheckoutForm checkoutForm, AlertMessage alertMessage) {
		var pageBuilder = CheckoutPageModel.builder();

		return pageBuilder
				.checkoutForm(checkoutForm)
				.creditCards(loadCreditCards())
				.alertMessage(alertMessage)
				.paymentProviderCreditCardTokenUrl(ecommerceProperties.getPaymentProviderCreditCardTokenUrl())
				.paymentProviderPublicKey(ecommerceProperties.getPaymentProviderPublicKey())
				.build()
				.toModelAndView();
	}

	@PostMapping("/checkout")
	public ModelAndView doCheckout(@Valid @ModelAttribute("checkoutForm") CheckoutForm checkoutForm,
								   BindingResult bindingResult, @AuthenticationPrincipal OAuth2User userDetails,
								   RedirectAttributes redirectAttributes) {
		ShoppingCartModel shoppingCart = shoppingCartService.findCurrentShoppingCart();

		if (shoppingCart.getTotalItems() < 1) {
			return new ModelAndView(REDIRECT_SHOPPING_CART);
		}

		if (shoppingCart.containsUnavailableItems()) {
			return redirectToCartWithUnavailableItemsAlert(redirectAttributes);
		}

		if (bindingResult.hasErrors()) {
			return checkout(checkoutForm, AlertMessage.danger("There are errors in the form!"));
		}

		String creditCardId;
		try {
			creditCardId = resolveCreditCardId(checkoutForm);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return checkout(checkoutForm, AlertMessage.danger("Credit card could not be used. Check the information and try again."));
		}

		CheckoutModel input = buildCheckoutInput(checkoutForm, creditCardId, userDetails);

		try {
			OrderModel checkout = checkoutClient.checkout(input);
			return new ModelAndView("redirect:/my-account/orders/" + checkout.getId());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return checkout(checkoutForm, AlertMessage.danger("Order could not be placed. Please try again."));
		}
	}

	private ModelAndView redirectToCartWithUnavailableItemsAlert(RedirectAttributes redirectAttributes) {
		redirectAttributes.addFlashAttribute(ALERT_MESSAGE_KEY, AlertMessage.danger(
				"Some items in your cart are no longer available. Remove them to continue with your order."));
		return new ModelAndView(REDIRECT_SHOPPING_CART);
	}

	@GetMapping("/buy-now/{productId}")
	public ModelAndView buyNowCheckout(@PathVariable String productId,
									   @RequestParam(value = "quantity", defaultValue = "1") Integer quantity,
									   RedirectAttributes redirectAttributes) {
		ProductModel product = loadProduct(productId, redirectAttributes);
		if (product == null) {
			return new ModelAndView("redirect:/products");
		}

		if (!Boolean.TRUE.equals(product.getInStock())) {
			redirectAttributes.addFlashAttribute(ALERT_MESSAGE_KEY, AlertMessage.danger("Product is out of stock."));
			return redirectToProduct(product);
		}

		if (quantity == null || quantity < 1) {
			redirectAttributes.addFlashAttribute(ALERT_MESSAGE_KEY, AlertMessage.danger("Quantity must be greater than zero."));
			return redirectToProduct(product);
		}

		CustomerModel customerModel;

		try {
			customerModel = customerManagementAPIClient.getMyProfile();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			BuyNowCheckoutForm form = new BuyNowCheckoutForm();
			form.setProductId(productId);
			form.setQuantity(quantity);
			return buyNowCheckout(form, product, null);
		}

		return buyNowCheckout(BuyNowCheckoutForm.of(customerModel, productId, quantity), product, null);
	}

	private ModelAndView buyNowCheckout(BuyNowCheckoutForm buyNowCheckoutForm, ProductModel product,
										AlertMessage alertMessage) {
		BigDecimal unitPrice = productPrice(product);
		BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(safeQuantity(buyNowCheckoutForm.getQuantity())));

		return BuyNowCheckoutPageModel.builder()
				.buyNowCheckoutForm(buyNowCheckoutForm)
				.product(product)
				.unitPrice(unitPrice)
				.subtotal(subtotal)
				.creditCards(loadCreditCards())
				.alertMessage(alertMessage)
				.paymentProviderCreditCardTokenUrl(ecommerceProperties.getPaymentProviderCreditCardTokenUrl())
				.paymentProviderPublicKey(ecommerceProperties.getPaymentProviderPublicKey())
				.build()
				.toModelAndView();
	}

	@PostMapping("/buy-now")
	public ModelAndView doBuyNowCheckout(@Valid @ModelAttribute("buyNowCheckoutForm") BuyNowCheckoutForm buyNowCheckoutForm,
										 BindingResult bindingResult,
										 @AuthenticationPrincipal OAuth2User userDetails,
										 RedirectAttributes redirectAttributes) {
		ProductModel product = loadProduct(buyNowCheckoutForm.getProductId(), redirectAttributes);
		if (product == null) {
			return new ModelAndView("redirect:/products");
		}

		if (!Boolean.TRUE.equals(product.getInStock())) {
			redirectAttributes.addFlashAttribute(ALERT_MESSAGE_KEY, AlertMessage.danger("Product is out of stock."));
			return redirectToProduct(product);
		}

		if (bindingResult.hasErrors() || buyNowCheckoutForm.getQuantity() == null || buyNowCheckoutForm.getQuantity() < 1) {
			return buyNowCheckout(buyNowCheckoutForm, product, AlertMessage.danger("There are errors in the form!"));
		}

		String creditCardId;
		try {
			creditCardId = resolveCreditCardId(buyNowCheckoutForm);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return buyNowCheckout(buyNowCheckoutForm, product,
					AlertMessage.danger("Credit card could not be used. Check the information and try again."));
		}

		CheckoutModel checkoutInput = buildCheckoutInput(buyNowCheckoutForm, creditCardId, userDetails);
		BuyNowCheckoutModel input = BuyNowCheckoutModel.builder()
				.productId(buyNowCheckoutForm.getProductId())
				.quantity(buyNowCheckoutForm.getQuantity())
				.paymentMethod(checkoutInput.getPaymentMethod())
				.creditCardId(checkoutInput.getCreditCardId())
				.shipping(checkoutInput.getShipping())
				.billing(checkoutInput.getBilling())
				.build();

		try {
			OrderModel checkout = checkoutClient.buyNow(input);
			return new ModelAndView("redirect:/my-account/orders/" + checkout.getId());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return buyNowCheckout(buyNowCheckoutForm, product,
					AlertMessage.danger("Order could not be placed. Please try again."));
		}
	}

	@PostMapping("/checkout/shipping-cost-preview")
	public ResponseEntity<ShippingCostPreviewResponse> previewShippingCost(
			@RequestBody @Valid ShippingCostPreviewInput input, BindingResult bindingResult) {
		if (bindingResult.hasErrors() || isInvalidZipCode(input)) {
			return ResponseEntity.badRequest().build();
		}

		try {
			ShippingCostPreviewModel preview = shippingCostClient.preview(input);
			BigDecimal cost = preview == null || preview.getCost() == null ? BigDecimal.ZERO : preview.getCost();
			BigDecimal subtotal = currentShoppingCartTotalAmount();
			BigDecimal totalAmount = subtotal.add(cost);

			return ResponseEntity.ok(ShippingCostPreviewResponse.builder()
					.cost(cost)
					.expectedDate(preview == null ? null : preview.getExpectedDate())
					.formattedCost(formatCurrency(cost))
					.formattedTotalAmount(formatCurrency(totalAmount))
					.build());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return ResponseEntity.badRequest().build();
		}
	}

	@PostMapping("/buy-now/shipping-cost-preview")
	public ResponseEntity<ShippingCostPreviewResponse> previewBuyNowShippingCost(
			@RequestBody @Valid BuyNowShippingCostPreviewInput input, BindingResult bindingResult) {
		if (bindingResult.hasErrors()
				|| isInvalidZipCode(input.getZipCode())
				|| input.getQuantity() == null
				|| input.getQuantity() < 1
				|| !StringUtils.hasText(input.getProductId())) {
			return ResponseEntity.badRequest().build();
		}

		try {
			ProductModel product = productClient.findById(input.getProductId());
			ShippingCostPreviewModel preview = shippingCostClient.preview(ShippingCostPreviewInput.builder()
					.zipCode(input.getZipCode())
					.build());
			BigDecimal cost = preview == null || preview.getCost() == null ? BigDecimal.ZERO : preview.getCost();
			BigDecimal subtotal = productPrice(product).multiply(BigDecimal.valueOf(input.getQuantity()));
			BigDecimal totalAmount = subtotal.add(cost);

			return ResponseEntity.ok(ShippingCostPreviewResponse.builder()
					.cost(cost)
					.expectedDate(preview == null ? null : preview.getExpectedDate())
					.formattedCost(formatCurrency(cost))
					.formattedTotalAmount(formatCurrency(totalAmount))
					.build());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return ResponseEntity.badRequest().build();
		}
	}

	private CheckoutModel buildCheckoutInput(CheckoutForm checkoutForm, String creditCardId, OAuth2User userDetails) {
		PersonalInfoModel shippingInfo = checkoutForm.getShippingInfo();
		PersonalInfoModel billingInfo = checkoutForm.isBillToDifferentAddress()
				? checkoutForm.getBillingInfo()
				: shippingInfo;

		var billingName = FullNameParser.split(billingInfo.getFullName());
		var shippingName = FullNameParser.split(shippingInfo.getFullName());

		String email = userDetails.getAttribute("email");
		if (email == null || email.isBlank()) {
			email = customerManagementAPIClient.getMyProfile().getEmail();
		}

		return CheckoutModel.builder()
				.paymentMethod(checkoutForm.getPaymentMethod().name())
				.creditCardId(creditCardId)
				.shipping(ShippingInputModel.builder()
						.recipient(RecipientModel.builder()
								.firstName(shippingName.firstName())
								.lastName(shippingName.lastName())
								.document(shippingInfo.getDocument())
								.phone(shippingInfo.getPhone())
								.build())
						.address(shippingInfo.getAddress())
						.build())
				.billing(BillingModel.builder()
						.firstName(billingName.firstName())
						.lastName(billingName.lastName())
						.document(billingInfo.getDocument())
						.phone(billingInfo.getPhone())
						.email(email)
						.address(billingInfo.getAddress())
						.build())
				.build();
	}

	private String resolveCreditCardId(CheckoutForm checkoutForm) {
		if (!PaymentMethod.CREDIT_CARD.equals(checkoutForm.getPaymentMethod())) {
			return null;
		}

		if (StringUtils.hasText(checkoutForm.getCreditCardId())) {
			creditCardClient.findById(checkoutForm.getCreditCardId());
			return checkoutForm.getCreditCardId();
		}

		throw new IllegalArgumentException("Credit card id is required");
	}

	private List<CreditCardModel> loadCreditCards() {
		try {
			List<CreditCardModel> creditCards = creditCardClient.findAll();
			return creditCards == null ? List.of() : creditCards;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return List.of();
		}
	}

	private boolean isInvalidZipCode(ShippingCostPreviewInput input) {
		return input == null
				|| isInvalidZipCode(input.getZipCode());
	}

	private boolean isInvalidZipCode(String zipCode) {
		return !StringUtils.hasText(zipCode)
				|| !zipCode.matches("\\d{5}");
	}

	private BigDecimal currentShoppingCartTotalAmount() {
		ShoppingCartModel shoppingCart = shoppingCartService.findCurrentShoppingCart();
		if (shoppingCart == null || shoppingCart.getTotalAmount() == null) {
			return BigDecimal.ZERO;
		}
		return shoppingCart.getTotalAmount();
	}

	private String formatCurrency(BigDecimal value) {
		return NumberFormat.getCurrencyInstance(LocaleContextHolder.getLocale()).format(value);
	}

	private ProductModel loadProduct(String productId, RedirectAttributes redirectAttributes) {
		if (!StringUtils.hasText(productId)) {
			redirectAttributes.addFlashAttribute(ALERT_MESSAGE_KEY, AlertMessage.danger("Product not found."));
			return null;
		}

		try {
			return productClient.findById(productId);
		} catch (HttpClientErrorException.NotFound e) {
			redirectAttributes.addFlashAttribute(ALERT_MESSAGE_KEY, AlertMessage.danger("Product not found."));
			return null;
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			redirectAttributes.addFlashAttribute(ALERT_MESSAGE_KEY, AlertMessage.danger(
					"An unknown error occurred while trying to load the product. Please try again later."));
			return null;
		}
	}

	private ModelAndView redirectToProduct(ProductModel product) {
		return new ModelAndView(String.format("redirect:/products/%s/%s", product.getSlug(), product.getId()));
	}

	private BigDecimal productPrice(ProductModel product) {
		BigDecimal price = Boolean.TRUE.equals(product.getHasDiscount())
				? product.getSalePrice()
				: product.getRegularPrice();
		return price == null ? BigDecimal.ZERO : price;
	}

	private int safeQuantity(Integer quantity) {
		return quantity == null || quantity < 1 ? 1 : quantity;
	}

}
