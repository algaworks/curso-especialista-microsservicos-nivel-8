package com.algaworks.algashop.ecommerce.application.model.client;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ShoppingCartModelTest {

	@Test
	void shouldNotContainUnavailableItemsWhenCartIsEmpty() {
		assertThat(new ShoppingCartModel().containsUnavailableItems()).isFalse();
	}

	@Test
	void shouldNotContainUnavailableItemsWhenAllItemsAreAvailable() {
		ShoppingCartModel shoppingCart = shoppingCartWith(item(true), item(true));

		assertThat(shoppingCart.containsUnavailableItems()).isFalse();
	}

	@Test
	void shouldContainUnavailableItemsWhenAnyItemIsUnavailable() {
		ShoppingCartModel shoppingCart = shoppingCartWith(item(true), item(false));

		assertThat(shoppingCart.containsUnavailableItems()).isTrue();
	}

	@Test
	void shouldTreatUnknownAvailabilityAsAvailable() {
		ShoppingCartModel shoppingCart = shoppingCartWith(item(null));

		assertThat(shoppingCart.containsUnavailableItems()).isFalse();
		assertThat(shoppingCart.getItems().getFirst().isUnavailable()).isFalse();
	}

	private ShoppingCartModel shoppingCartWith(ShoppingCartItemModel... items) {
		ShoppingCartModel shoppingCart = new ShoppingCartModel();
		shoppingCart.setItems(List.of(items));
		return shoppingCart;
	}

	private ShoppingCartItemModel item(Boolean available) {
		ShoppingCartItemModel item = new ShoppingCartItemModel();
		item.setName("Product Name");
		item.setPrice(new BigDecimal("100.00"));
		item.setQuantity(1);
		item.setAvailable(available);
		return item;
	}
}
