package com.algaworks.algashop.ecommerce.application.client;

import com.algaworks.algashop.ecommerce.application.model.client.ShoppingCartItemInput;
import com.algaworks.algashop.ecommerce.application.model.client.ShoppingCartItemListModel;
import com.algaworks.algashop.ecommerce.application.model.client.ShoppingCartModel;
import com.algaworks.algashop.ecommerce.application.properties.EcommerceProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.net.URI;

@Component
public class ShoppingCartClient {

	private static final String SHOPPING_CART_URI = "/api/v1/customers/me/shopping-cart";
	private static final String SHOPPING_CART_ITEMS_URI = "/api/v1/customers/me/shopping-cart/items";

	private final EcommerceProperties properties;
	private final RestClient restClient;

	public ShoppingCartClient(EcommerceProperties properties,
				  @Qualifier("userAuthenticatedRestClient") RestClient restClient) {
		this.properties = properties;
		this.restClient = restClient;
	}

	public ShoppingCartModel getCurrentShoppingCart() {
		return restClient.get()
				.uri(URI.create(properties.getApiUrl() + SHOPPING_CART_URI))
				.accept(MediaType.APPLICATION_JSON)
				.header("Cache-Control", "no-cache")
				.retrieve()
				.toEntity(ShoppingCartModel.class).getBody();
	}

	public ShoppingCartModel createCurrentShoppingCart() {
		return restClient.post()
				.uri(URI.create(properties.getApiUrl() + SHOPPING_CART_URI))
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.toEntity(ShoppingCartModel.class).getBody();
	}

	public ShoppingCartItemListModel getShoppingCartItems() {
		return restClient.get()
				.uri(URI.create(properties.getApiUrl() + SHOPPING_CART_ITEMS_URI))
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.toEntity(ShoppingCartItemListModel.class).getBody();
	}

	public void addItem(ShoppingCartItemInput input) {
		restClient.post()
				.uri(URI.create(properties.getApiUrl() + SHOPPING_CART_ITEMS_URI))
				.body(input)
				.accept(MediaType.APPLICATION_JSON)
				.retrieve().toBodilessEntity();
	}

	public void removeItem(String itemId) {
		restClient.delete()
				.uri(URI.create(properties.getApiUrl() + SHOPPING_CART_ITEMS_URI + "/" + itemId))
				.accept(MediaType.APPLICATION_JSON)
				.retrieve().toBodilessEntity();
	}

	public void empty() {
		restClient.delete()
				.uri(URI.create(properties.getApiUrl() + SHOPPING_CART_ITEMS_URI))
				.accept(MediaType.APPLICATION_JSON)
				.retrieve().toBodilessEntity();
	}
}
