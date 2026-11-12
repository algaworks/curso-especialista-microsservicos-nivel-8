package com.algaworks.algashop.ecommerce.application.controller;

import com.algaworks.algashop.ecommerce.application.client.ProductClient;
import com.algaworks.algashop.ecommerce.application.model.client.ProductModel;
import com.algaworks.algashop.ecommerce.application.model.client.ShoppingCartItemInput;
import com.algaworks.algashop.ecommerce.application.model.page.AlertMessage;
import com.algaworks.algashop.ecommerce.application.model.page.ShoppingCartPageModel;
import com.algaworks.algashop.ecommerce.application.service.ShoppingCartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ShoppingCartController {

	private static final String ALERT_MESSAGE_KEY = "alertMessage";

	private final ProductClient productClient;
	private final ShoppingCartService shoppingCartClient;

	//@CookieValue("guestCartId") String cookie   //@SessionAttribute
	@GetMapping("/shopping-cart")
	public ModelAndView index(@RequestParam(value = "removed", defaultValue = "false", required = false) Boolean removed) {
		var pageBuilder = ShoppingCartPageModel.builder();
		return pageBuilder
				.removed(removed)
				.build()
				.toModelAndView();
	}

	@PostMapping("/shopping-cart/remove/{productId}")
	public ResponseEntity<Void> removeItem(@PathVariable String productId) {
		shoppingCartClient.removeItem(productId);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/shopping-cart/remove-item/{itemId}")
	public ResponseEntity<Void> removeItemByItemId(@PathVariable String itemId) {
		shoppingCartClient.removeItem(itemId);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/shopping-cart/add/{slug}/{productId}")
	public RedirectView addItemById(@PathVariable String slug,
									@PathVariable String productId,
									@RequestParam Integer quantity,
									RedirectAttributes redirectAttributes) {
		ProductModel productModel = productClient.findById(productId);

		try {
			shoppingCartClient.addItem(new ShoppingCartItemInput(productId, quantity));
			redirectAttributes.addFlashAttribute(ALERT_MESSAGE_KEY, AlertMessage.success("Product added to the cart!"));
		} catch (HttpClientErrorException.BadRequest e) {
			ProblemDetail problemDetail = e.getResponseBodyAs(ProblemDetail.class);
			log.warn(e.getMessage(), e);
			redirectAttributes.addFlashAttribute(ALERT_MESSAGE_KEY, AlertMessage.danger(problemDetail.getDetail()));
		} catch (Exception _) {
			log.warn("Error adding item to cart", new Exception());
			redirectAttributes.addFlashAttribute(ALERT_MESSAGE_KEY, AlertMessage.danger(
					"An unknown error occurred while trying to add the item to the cart. Please try again later."
			));
		}

		return new RedirectView(String.format("/products/%s/%s", productModel.getSlug(), productModel.getId()));
	}
}
