package com.algaworks.algashop.ecommerce.application.controller;

import com.algaworks.algashop.ecommerce.application.model.client.ShoppingCartItemModel;
import com.algaworks.algashop.ecommerce.application.model.client.ShoppingCartModel;
import com.algaworks.algashop.ecommerce.application.model.page.AlertMessage;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.expression.ThymeleafEvaluationContext;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Renders the cart fragment for real so the availability expressions are exercised,
 * not just asserted as text.
 */
class ShoppingCartDetailTemplateTest {

	@Test
	void shouldNotFlagAnythingWhenAllItemsAreAvailable() {
		String html = render(shoppingCart(item("Keyboard", true)));

		assertThat(html)
				.doesNotContain("Out of stock")
				.doesNotContain("shopping-cart-item-unavailable")
				.doesNotContain("no longer available")
				.contains("href=\"/checkout\"");
	}

	@Test
	void shouldFlagUnavailableItemAndDisableCheckout() {
		String html = render(shoppingCart(item("Keyboard", true), item("Mouse", false)));

		assertThat(html)
				.contains("Some items in your cart are no longer available")
				.contains("shopping-cart-item-unavailable")
				.contains("Out of stock")
				.contains("checkout-btn mt-2 disabled")
				.doesNotContain("href=\"/checkout\"");
	}

	@Test
	void shouldRenderFlashAlertWithoutNestingADocumentShell() {
		String html = render(shoppingCart(item("Keyboard", true)),
				Map.of("alertMessage", AlertMessage.danger("Some items in your cart are no longer available.")));

		assertThat(html)
				.contains("<div class=\"alert alert-danger\" role=\"alert\">Some items in your cart are no longer available.</div>");
		assertThat(html.toLowerCase(Locale.ROOT).indexOf("<!doctype"))
				.isEqualTo(html.toLowerCase(Locale.ROOT).lastIndexOf("<!doctype"));
	}

	@Test
	void shouldFlagOnlyTheUnavailableRow() {
		String html = render(shoppingCart(item("Keyboard", true), item("Mouse", false)));

		String availableRow = html.substring(html.indexOf("Keyboard"), html.indexOf("Mouse"));

		assertThat(availableRow).doesNotContain("Out of stock");
		assertThat(html.indexOf("shopping-cart-item-unavailable\"")).isLessThan(html.indexOf("Mouse"));
	}

	/**
	 * checkout.html and 7 other pages insert this fragment as a whole template; adding
	 * th:fragment to it for the cart page must not change what they render.
	 */
	@Test
	void shouldStillRenderMessagesFragmentAsAWholeTemplate() {
		String html = render("fragments/messages-by-type", shoppingCart(item("Keyboard", true)),
				Map.of("alertMessage", AlertMessage.warning("Heads up")));

		assertThat(html)
				.contains("<div class=\"alert alert-warning\" role=\"alert\">Heads up</div>")
				.doesNotContain("th:fragment")
				.doesNotContain("messages");
	}

	private String render(ShoppingCartModel shoppingCart) {
		return render(shoppingCart, Map.of());
	}

	private String render(ShoppingCartModel shoppingCart, Map<String, Object> extraVariables) {
		return render("fragments/shopping-cart-detail", shoppingCart, extraVariables);
	}

	private String render(String templateName, ShoppingCartModel shoppingCart, Map<String, Object> extraVariables) {
		MockServletContext servletContext = new MockServletContext();

		GenericApplicationContext applicationContext = new GenericApplicationContext();
		applicationContext.registerBean("menuService", StubMenuService.class, () -> new StubMenuService(shoppingCart));
		applicationContext.refresh();

		SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
		templateResolver.setApplicationContext(applicationContext);
		templateResolver.setPrefix("classpath:/templates/");
		templateResolver.setSuffix(".html");

		SpringTemplateEngine templateEngine = new SpringTemplateEngine();
		templateEngine.setTemplateResolver(templateResolver);

		var webApplication = JakartaServletWebApplication.buildApplication(servletContext);
		var exchange = webApplication.buildExchange(new MockHttpServletRequest(servletContext),
				new MockHttpServletResponse());

		// ThymeleafView normally publishes this so SpEL can resolve @beanName references.
		Map<String, Object> variables = new HashMap<>();
		variables.put("removed", Boolean.FALSE);
		variables.putAll(extraVariables);
		variables.put(ThymeleafEvaluationContext.THYMELEAF_EVALUATION_CONTEXT_CONTEXT_VARIABLE_NAME,
				new ThymeleafEvaluationContext(applicationContext, null));

		var context = new WebContext(exchange, Locale.US, variables);

		return templateEngine.process(templateName, context);
	}

	private ShoppingCartModel shoppingCart(ShoppingCartItemModel... items) {
		ShoppingCartModel shoppingCart = new ShoppingCartModel();
		shoppingCart.setItems(List.of(items));
		shoppingCart.setTotalItems(items.length);
		shoppingCart.setTotalAmount(new BigDecimal("100.00"));
		return shoppingCart;
	}

	private ShoppingCartItemModel item(String name, boolean available) {
		ShoppingCartItemModel item = new ShoppingCartItemModel();
		item.setId("item-" + name);
		item.setProductId("product-" + name);
		item.setName(name);
		item.setPrice(new BigDecimal("50.00"));
		item.setQuantity(1);
		item.setTotalAmount(new BigDecimal("50.00"));
		item.setAvailable(available);
		return item;
	}

	public static class StubMenuService {

		private final ShoppingCartModel shoppingCart;

		StubMenuService(ShoppingCartModel shoppingCart) {
			this.shoppingCart = shoppingCart;
		}

		public ShoppingCartModel loadShoppingCart() {
			return shoppingCart;
		}
	}
}
