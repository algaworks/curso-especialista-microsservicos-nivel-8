package com.algaworks.algashop.ecommerce.application.controller;

import com.algaworks.algashop.ecommerce.application.client.*;
import com.algaworks.algashop.ecommerce.application.model.client.*;
import com.algaworks.algashop.ecommerce.application.model.form.BuyNowCheckoutForm;
import com.algaworks.algashop.ecommerce.application.model.form.CheckoutForm;
import com.algaworks.algashop.ecommerce.application.model.form.PaymentMethod;
import com.algaworks.algashop.ecommerce.application.model.page.AlertMessage;
import com.algaworks.algashop.ecommerce.application.properties.EcommerceProperties;
import com.algaworks.algashop.ecommerce.application.service.ShoppingCartService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CheckoutControllerTest {

	@Mock
	private ShoppingCartService shoppingCartService;

	@Mock
	private CheckoutClient checkoutClient;

	@Mock
	private CreditCardClient creditCardClient;

	@Mock
	private CustomerRestClient customerRestClient;

	@Mock
	private ShippingCostClient shippingCostClient;

	@Mock
	private ProductClient productClient;

	@AfterEach
	void tearDown() {
		LocaleContextHolder.resetLocaleContext();
	}

	@Test
	void shouldLoadCreditCardsAndPreserveCustomerDataOnCheckoutPage() {
		CheckoutController controller = controller();
		CreditCardModel creditCard = creditCard("card-1");

		when(shoppingCartService.findCurrentShoppingCart()).thenReturn(shoppingCart());
		when(customerRestClient.getMyProfile()).thenReturn(customer());
		when(creditCardClient.findAll()).thenReturn(List.of(creditCard));

		ModelAndView modelAndView = controller.checkout(new RedirectAttributesModelMap());

		assertThat(modelAndView.getViewName()).isEqualTo("checkout");
		CheckoutForm form = (CheckoutForm) modelAndView.getModel().get("checkoutForm");
		assertThat(form.getShippingInfo().getFullName()).isEqualTo("Alex Silva");
		assertThat(form.getShippingInfo().getAddress().getZipCode()).isEqualTo("12345");
		assertThat(form.getBillingInfo().getFullName()).isNull();
		assertThat(form.getBillingInfo().getAddress().getZipCode()).isNull();
		assertThat(modelAndView.getModel().get("creditCards")).isEqualTo(List.of(creditCard));
	}

	@Test
	void shouldCheckoutWithSavedCreditCard() {
		CheckoutController controller = controller();
		CheckoutForm form = checkoutForm(PaymentMethod.CREDIT_CARD);
		form.setCreditCardId("card-1");
		ArgumentCaptor<CheckoutModel> inputCaptor = ArgumentCaptor.forClass(CheckoutModel.class);

		when(shoppingCartService.findCurrentShoppingCart()).thenReturn(shoppingCart());
		when(creditCardClient.findById("card-1")).thenReturn(creditCard("card-1"));
		when(checkoutClient.checkout(any(CheckoutModel.class))).thenReturn(order("order-1"));

		ModelAndView modelAndView = controller.doCheckout(form, bindingResult(form), oauth2User(), new RedirectAttributesModelMap());

		assertThat(modelAndView.getViewName()).isEqualTo("redirect:/my-account/orders/order-1");
		verify(creditCardClient).findById("card-1");
		verify(checkoutClient).checkout(inputCaptor.capture());
		assertThat(inputCaptor.getValue().getCreditCardId()).isEqualTo("card-1");
		assertThat(inputCaptor.getValue().getPaymentMethod()).isEqualTo("CREDIT_CARD");
	}

	@Test
	void shouldCopyShippingInfoToBillingWhenBillingAddressIsNotDifferent() {
		CheckoutController controller = controller();
		CheckoutForm form = checkoutForm(PaymentMethod.GATEWAY_BALANCE);
		form.setShippingInfo(personalInfo("Shipping Person", "111-22-333", "111-222-3333", address("10000", "Shipping Street")));
		form.setBillingInfo(personalInfo("Billing Person", "999-88-777", "999-888-7777", address("90000", "Billing Street")));
		ArgumentCaptor<CheckoutModel> inputCaptor = ArgumentCaptor.forClass(CheckoutModel.class);

		when(shoppingCartService.findCurrentShoppingCart()).thenReturn(shoppingCart());
		when(checkoutClient.checkout(any(CheckoutModel.class))).thenReturn(order("order-1"));

		ModelAndView modelAndView = controller.doCheckout(form, bindingResult(form), oauth2User(), new RedirectAttributesModelMap());

		assertThat(modelAndView.getViewName()).isEqualTo("redirect:/my-account/orders/order-1");
		verify(checkoutClient).checkout(inputCaptor.capture());
		CheckoutModel input = inputCaptor.getValue();
		assertThat(input.getShipping().getRecipient().getFirstName()).isEqualTo("Shipping");
		assertThat(input.getShipping().getRecipient().getLastName()).isEqualTo("Person");
		assertThat(input.getBilling().getFirstName()).isEqualTo("Shipping");
		assertThat(input.getBilling().getLastName()).isEqualTo("Person");
		assertThat(input.getBilling().getDocument()).isEqualTo("111-22-333");
		assertThat(input.getBilling().getPhone()).isEqualTo("111-222-3333");
		assertThat(input.getBilling().getAddress().getStreet()).isEqualTo("Shipping Street");
	}

	@Test
	void shouldUseBillingInfoWhenBillingAddressIsDifferent() {
		CheckoutController controller = controller();
		CheckoutForm form = checkoutForm(PaymentMethod.GATEWAY_BALANCE);
		form.setBillToDifferentAddress(true);
		form.setShippingInfo(personalInfo("Shipping Person", "111-22-333", "111-222-3333", address("10000", "Shipping Street")));
		form.setBillingInfo(personalInfo("Billing Person", "999-88-777", "999-888-7777", address("90000", "Billing Street")));
		ArgumentCaptor<CheckoutModel> inputCaptor = ArgumentCaptor.forClass(CheckoutModel.class);

		when(shoppingCartService.findCurrentShoppingCart()).thenReturn(shoppingCart());
		when(checkoutClient.checkout(any(CheckoutModel.class))).thenReturn(order("order-1"));

		ModelAndView modelAndView = controller.doCheckout(form, bindingResult(form), oauth2User(), new RedirectAttributesModelMap());

		assertThat(modelAndView.getViewName()).isEqualTo("redirect:/my-account/orders/order-1");
		verify(checkoutClient).checkout(inputCaptor.capture());
		CheckoutModel input = inputCaptor.getValue();
		assertThat(input.getShipping().getRecipient().getFirstName()).isEqualTo("Shipping");
		assertThat(input.getShipping().getAddress().getStreet()).isEqualTo("Shipping Street");
		assertThat(input.getBilling().getFirstName()).isEqualTo("Billing");
		assertThat(input.getBilling().getLastName()).isEqualTo("Person");
		assertThat(input.getBilling().getDocument()).isEqualTo("999-88-777");
		assertThat(input.getBilling().getPhone()).isEqualTo("999-888-7777");
		assertThat(input.getBilling().getAddress().getStreet()).isEqualTo("Billing Street");
	}

	@Test
	void shouldReturnCheckoutWithAlertWhenCreditCardHasNoIdAndNoToken() {
		CheckoutController controller = controller();
		CheckoutForm form = checkoutForm(PaymentMethod.CREDIT_CARD);

		when(shoppingCartService.findCurrentShoppingCart()).thenReturn(shoppingCart());

		ModelAndView modelAndView = controller.doCheckout(form, bindingResult(form), oauth2User(), new RedirectAttributesModelMap());

		assertThat(modelAndView.getViewName()).isEqualTo("checkout");
		AlertMessage alertMessage = (AlertMessage) modelAndView.getModel().get("alertMessage");
		assertThat(alertMessage.getType()).isEqualTo(AlertMessage.Type.DANGER);
		verify(creditCardClient, never()).register(any());
		verify(checkoutClient, never()).checkout(any(CheckoutModel.class));
	}

	@Test
	void shouldReturnCheckoutWithAlertWhenCreditCardValidationFails() {
		CheckoutController controller = controller();
		CheckoutForm form = checkoutForm(PaymentMethod.CREDIT_CARD);
		form.setCreditCardId("card-1");

		when(shoppingCartService.findCurrentShoppingCart()).thenReturn(shoppingCart());
		when(creditCardClient.findById("card-1")).thenThrow(new RuntimeException("not found"));

		ModelAndView modelAndView = controller.doCheckout(form, bindingResult(form), oauth2User(), new RedirectAttributesModelMap());

		assertThat(modelAndView.getViewName()).isEqualTo("checkout");
		AlertMessage alertMessage = (AlertMessage) modelAndView.getModel().get("alertMessage");
		assertThat(alertMessage.getType()).isEqualTo(AlertMessage.Type.DANGER);
		verify(creditCardClient, never()).register(any());
		verify(checkoutClient, never()).checkout(any(CheckoutModel.class));
	}

	@Test
	void shouldRedirectToShoppingCartWithAlertWhenCheckoutPageHasUnavailableItems() {
		CheckoutController controller = controller();
		RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

		when(shoppingCartService.findCurrentShoppingCart()).thenReturn(shoppingCartWithUnavailableItem());

		ModelAndView modelAndView = controller.checkout(redirectAttributes);

		assertThat(modelAndView.getViewName()).isEqualTo("redirect:/shopping-cart");
		AlertMessage alertMessage = (AlertMessage) redirectAttributes.getFlashAttributes().get("alertMessage");
		assertThat(alertMessage.getType()).isEqualTo(AlertMessage.Type.DANGER);
		verifyNoInteractions(customerRestClient);
	}

	@Test
	void shouldRedirectToShoppingCartWithAlertWhenPlacingOrderWithUnavailableItems() {
		CheckoutController controller = controller();
		CheckoutForm form = checkoutForm(PaymentMethod.GATEWAY_BALANCE);
		RedirectAttributesModelMap redirectAttributes = new RedirectAttributesModelMap();

		when(shoppingCartService.findCurrentShoppingCart()).thenReturn(shoppingCartWithUnavailableItem());

		ModelAndView modelAndView = controller.doCheckout(form, bindingResult(form), oauth2User(), redirectAttributes);

		assertThat(modelAndView.getViewName()).isEqualTo("redirect:/shopping-cart");
		AlertMessage alertMessage = (AlertMessage) redirectAttributes.getFlashAttributes().get("alertMessage");
		assertThat(alertMessage.getType()).isEqualTo(AlertMessage.Type.DANGER);
		verifyNoInteractions(checkoutClient);
	}

	@Test
	void shouldReturnCheckoutWithAlertWhenOrderingFails() {
		CheckoutController controller = controller();
		CheckoutForm form = checkoutForm(PaymentMethod.GATEWAY_BALANCE);

		when(shoppingCartService.findCurrentShoppingCart()).thenReturn(shoppingCart());
		when(checkoutClient.checkout(any(CheckoutModel.class))).thenThrow(new RuntimeException("ordering unavailable"));

		ModelAndView modelAndView = controller.doCheckout(form, bindingResult(form), oauth2User(), new RedirectAttributesModelMap());

		assertThat(modelAndView.getViewName()).isEqualTo("checkout");
		AlertMessage alertMessage = (AlertMessage) modelAndView.getModel().get("alertMessage");
		assertThat(alertMessage.getType()).isEqualTo(AlertMessage.Type.DANGER);
	}

	@Test
	void shouldLoadBuyNowCheckoutPageWithProductAndCustomerData() {
		CheckoutController controller = controller();
		ProductModel product = product("product-1", new BigDecimal("25.00"));
		CreditCardModel creditCard = creditCard("card-1");

		when(productClient.findById("product-1")).thenReturn(product);
		when(customerRestClient.getMyProfile()).thenReturn(customer());
		when(creditCardClient.findAll()).thenReturn(List.of(creditCard));

		ModelAndView modelAndView = controller.buyNowCheckout(
				"product-1", 2, new RedirectAttributesModelMap());

		assertThat(modelAndView.getViewName()).isEqualTo("buy-now");
		BuyNowCheckoutForm form = (BuyNowCheckoutForm) modelAndView.getModel().get("buyNowCheckoutForm");
		assertThat(form.getProductId()).isEqualTo("product-1");
		assertThat(form.getQuantity()).isEqualTo(2);
		assertThat(form.getShippingInfo().getFullName()).isEqualTo("Alex Silva");
		assertThat(modelAndView.getModel().get("product")).isEqualTo(product);
		assertThat(modelAndView.getModel().get("unitPrice")).isEqualTo(new BigDecimal("25.00"));
		assertThat(modelAndView.getModel().get("subtotal")).isEqualTo(new BigDecimal("50.00"));
		assertThat(modelAndView.getModel().get("creditCards")).isEqualTo(List.of(creditCard));
	}

	@Test
	void shouldBuyNowWithGatewayBalance() {
		CheckoutController controller = controller();
		BuyNowCheckoutForm form = buyNowCheckoutForm(PaymentMethod.GATEWAY_BALANCE);
		ArgumentCaptor<BuyNowCheckoutModel> inputCaptor = ArgumentCaptor.forClass(BuyNowCheckoutModel.class);

		when(productClient.findById("product-1")).thenReturn(product("product-1", new BigDecimal("25.00")));
		when(checkoutClient.buyNow(any(BuyNowCheckoutModel.class))).thenReturn(order("order-1"));

		ModelAndView modelAndView = controller.doBuyNowCheckout(
				form, bindingResult(form, "buyNowCheckoutForm"), oauth2User(), new RedirectAttributesModelMap());

		assertThat(modelAndView.getViewName()).isEqualTo("redirect:/my-account/orders/order-1");
		verify(checkoutClient).buyNow(inputCaptor.capture());
		BuyNowCheckoutModel input = inputCaptor.getValue();
		assertThat(input.getProductId()).isEqualTo("product-1");
		assertThat(input.getQuantity()).isEqualTo(2);
		assertThat(input.getPaymentMethod()).isEqualTo("GATEWAY_BALANCE");
		assertThat(input.getShipping().getRecipient().getFirstName()).isEqualTo("Alex");
		assertThat(input.getBilling().getFirstName()).isEqualTo("Alex");
	}

	@Test
	void shouldValidateCreditCardBeforeBuyNow() {
		CheckoutController controller = controller();
		BuyNowCheckoutForm form = buyNowCheckoutForm(PaymentMethod.CREDIT_CARD);
		form.setCreditCardId("card-1");

		when(productClient.findById("product-1")).thenReturn(product("product-1", new BigDecimal("25.00")));
		when(creditCardClient.findById("card-1")).thenReturn(creditCard("card-1"));
		when(checkoutClient.buyNow(any(BuyNowCheckoutModel.class))).thenReturn(order("order-1"));

		ModelAndView modelAndView = controller.doBuyNowCheckout(
				form, bindingResult(form, "buyNowCheckoutForm"), oauth2User(), new RedirectAttributesModelMap());

		assertThat(modelAndView.getViewName()).isEqualTo("redirect:/my-account/orders/order-1");
		verify(creditCardClient).findById("card-1");
	}

	@Test
	void shouldReturnBuyNowCheckoutWithAlertWhenOrderingFails() {
		CheckoutController controller = controller();
		BuyNowCheckoutForm form = buyNowCheckoutForm(PaymentMethod.GATEWAY_BALANCE);

		when(productClient.findById("product-1")).thenReturn(product("product-1", new BigDecimal("25.00")));
		when(checkoutClient.buyNow(any(BuyNowCheckoutModel.class))).thenThrow(new RuntimeException("ordering unavailable"));

		ModelAndView modelAndView = controller.doBuyNowCheckout(
				form, bindingResult(form, "buyNowCheckoutForm"), oauth2User(), new RedirectAttributesModelMap());

		assertThat(modelAndView.getViewName()).isEqualTo("buy-now");
		AlertMessage alertMessage = (AlertMessage) modelAndView.getModel().get("alertMessage");
		assertThat(alertMessage.getType()).isEqualTo(AlertMessage.Type.DANGER);
	}

	@Test
	void shouldPreviewBuyNowShippingCostAndReturnFormattedAmounts() {
		LocaleContextHolder.setLocale(Locale.US);
		CheckoutController controller = controller();
		BuyNowShippingCostPreviewInput input = BuyNowShippingCostPreviewInput.builder()
				.zipCode("12345")
				.productId("product-1")
				.quantity(2)
				.build();

		when(productClient.findById("product-1")).thenReturn(product("product-1", new BigDecimal("25.00")));
		when(shippingCostClient.preview(ShippingCostPreviewInput.builder().zipCode("12345").build()))
				.thenReturn(ShippingCostPreviewModel.builder()
						.cost(new BigDecimal("12.34"))
						.expectedDate(LocalDate.of(2026, 6, 20))
						.build());

		ResponseEntity<ShippingCostPreviewResponse> response = controller.previewBuyNowShippingCost(
				input, bindingResult(input, "buyNowShippingCostPreviewInput"));

		assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().getFormattedCost()).isEqualTo("$12.34");
		assertThat(response.getBody().getFormattedTotalAmount()).isEqualTo("$62.34");
	}

	@Test
	void shouldPreviewShippingCostAndReturnFormattedAmounts() {
		LocaleContextHolder.setLocale(Locale.US);
		CheckoutController controller = controller();
		ShippingCostPreviewInput input = ShippingCostPreviewInput.builder()
				.zipCode("12345")
				.build();

		when(shoppingCartService.findCurrentShoppingCart()).thenReturn(shoppingCart());
		when(shippingCostClient.preview(input)).thenReturn(ShippingCostPreviewModel.builder()
				.cost(new BigDecimal("12.34"))
				.expectedDate(LocalDate.of(2026, 6, 20))
				.build());

		ResponseEntity<ShippingCostPreviewResponse> response = controller.previewShippingCost(
				input, bindingResult(input, "shippingCostPreviewInput"));

		assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().getCost()).isEqualByComparingTo("12.34");
		assertThat(response.getBody().getExpectedDate()).isEqualTo(LocalDate.of(2026, 6, 20));
		assertThat(response.getBody().getFormattedCost()).isEqualTo("$12.34");
		assertThat(response.getBody().getFormattedTotalAmount()).isEqualTo("$112.34");
	}

	@Test
	void shouldReturnBadRequestWhenShippingCostPreviewZipCodeIsInvalid() {
		CheckoutController controller = controller();
		ShippingCostPreviewInput input = ShippingCostPreviewInput.builder()
				.zipCode("123")
				.build();

		ResponseEntity<ShippingCostPreviewResponse> response = controller.previewShippingCost(
				input, bindingResult(input, "shippingCostPreviewInput"));

		assertThat(response.getStatusCode().is4xxClientError()).isTrue();
		verify(shippingCostClient, never()).preview(any(ShippingCostPreviewInput.class));
	}

	@Test
	void shouldReturnBadRequestWhenShippingCostPreviewFails() {
		CheckoutController controller = controller();
		ShippingCostPreviewInput input = ShippingCostPreviewInput.builder()
				.zipCode("12345")
				.build();

		when(shippingCostClient.preview(input)).thenThrow(new RuntimeException("unavailable"));

		ResponseEntity<ShippingCostPreviewResponse> response = controller.previewShippingCost(
				input, bindingResult(input, "shippingCostPreviewInput"));

		assertThat(response.getStatusCode().is4xxClientError()).isTrue();
	}

	private CheckoutController controller() {
		EcommerceProperties ecommerceProperties = new EcommerceProperties();
		ecommerceProperties.setPaymentProviderCreditCardTokenUrl("http://fastpay/tokenized-cards");
		ecommerceProperties.setPaymentProviderPublicKey("public-key");
		return new CheckoutController(shoppingCartService, checkoutClient, creditCardClient, customerRestClient,
				shippingCostClient, productClient, ecommerceProperties);
	}

	private BeanPropertyBindingResult bindingResult(CheckoutForm form) {
		return new BeanPropertyBindingResult(form, "checkoutForm");
	}

	private BeanPropertyBindingResult bindingResult(Object target, String objectName) {
		return new BeanPropertyBindingResult(target, objectName);
	}

	private ShoppingCartModel shoppingCart() {
		ShoppingCartModel shoppingCart = new ShoppingCartModel();
		shoppingCart.setTotalItems(1);
		shoppingCart.setTotalAmount(new BigDecimal("100.00"));
		return shoppingCart;
	}

	private ShoppingCartModel shoppingCartWithUnavailableItem() {
		ShoppingCartModel shoppingCart = shoppingCart();
		ShoppingCartItemModel item = new ShoppingCartItemModel();
		item.setId("item-1");
		item.setProductId("product-1");
		item.setName("Product Name");
		item.setPrice(new BigDecimal("100.00"));
		item.setQuantity(1);
		item.setTotalAmount(new BigDecimal("100.00"));
		item.setAvailable(false);
		shoppingCart.setItems(List.of(item));
		return shoppingCart;
	}

	private CustomerModel customer() {
		CustomerModel customer = new CustomerModel();
		customer.setFirstName("Alex");
		customer.setLastName("Silva");
		customer.setEmail("alex@example.com");
		customer.setPhone("123-456-7890");
		customer.setDocument("123-45-678");
		customer.setAddress(address());
		return customer;
	}

	private CheckoutForm checkoutForm(PaymentMethod paymentMethod) {
		return CheckoutForm.builder()
				.paymentMethod(paymentMethod)
				.billingInfo(personalInfo("Alex Silva"))
				.shippingInfo(personalInfo("Alex Silva"))
				.build();
	}

	private BuyNowCheckoutForm buyNowCheckoutForm(PaymentMethod paymentMethod) {
		BuyNowCheckoutForm form = new BuyNowCheckoutForm();
		form.setProductId("product-1");
		form.setQuantity(2);
		form.setPaymentMethod(paymentMethod);
		form.setBillingInfo(personalInfo("Alex Silva"));
		form.setShippingInfo(personalInfo("Alex Silva"));
		return form;
	}

	private ProductModel product(String id, BigDecimal price) {
		ProductModel product = new ProductModel();
		product.setId(id);
		product.setSlug("product-slug");
		product.setName("Product Name");
		product.setInStock(true);
		product.setHasDiscount(false);
		product.setRegularPrice(price);
		return product;
	}

	private PersonalInfoModel personalInfo(String fullName) {
		return personalInfo(fullName, "123-45-678", "123-456-7890", address());
	}

	private PersonalInfoModel personalInfo(String fullName, String document, String phone, AddressModel address) {
		return PersonalInfoModel.builder()
				.fullName(fullName)
				.document(document)
				.phone(phone)
				.address(address)
				.build();
	}

	private AddressModel address() {
		return address("12345", "Main Street");
	}

	private AddressModel address(String zipCode, String street) {
		return AddressModel.builder()
				.zipCode(zipCode)
				.street(street)
				.number("100")
				.complement("Apt 10")
				.neighborhood("Downtown")
				.city("Austin")
				.state("TX")
				.build();
	}

	private CreditCardModel creditCard(String id) {
		CreditCardModel creditCard = new CreditCardModel();
		creditCard.setId(id);
		creditCard.setBrand("Visa");
		creditCard.setLastNumbers("1234");
		creditCard.setExpMonth(12);
		creditCard.setExpYear(2028);
		return creditCard;
	}

	private OrderModel order(String id) {
		OrderModel order = new OrderModel();
		order.setId(id);
		return order;
	}

	private OAuth2User oauth2User() {
		return new DefaultOAuth2User(
				List.of(new SimpleGrantedAuthority("ROLE_USER")),
				Map.of("email", "alex@example.com", "name", "Alex Silva"),
				"email"
		);
	}
}
