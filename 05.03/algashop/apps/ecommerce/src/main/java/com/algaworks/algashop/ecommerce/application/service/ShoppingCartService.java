package com.algaworks.algashop.ecommerce.application.service;

import com.algaworks.algashop.ecommerce.application.client.ShoppingCartClient;
import com.algaworks.algashop.ecommerce.application.model.client.ShoppingCartItemInput;
import com.algaworks.algashop.ecommerce.application.model.client.ShoppingCartModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;


@Service
@RequiredArgsConstructor
@Slf4j
public class ShoppingCartService {

	private final ShoppingCartClient shoppingCartClient;

	public ShoppingCartModel findCurrentShoppingCart() {
		try {
			return shoppingCartClient.getCurrentShoppingCart();
		} catch (HttpClientErrorException e) {
			return new ShoppingCartModel();
		}
	}

	public void addItem(ShoppingCartItemInput shoppingCartItemInput) {
		try {
			shoppingCartClient.addItem(shoppingCartItemInput);
		} catch (HttpClientErrorException.NotFound e) {
			createCurrentShoppingCartIfNecessary();
			shoppingCartClient.addItem(shoppingCartItemInput);
		}
	}

	public void removeItem(String itemId) {
		shoppingCartClient.removeItem(itemId);
	}

	private void createCurrentShoppingCartIfNecessary() {
		try {
			shoppingCartClient.createCurrentShoppingCart();
		} catch (HttpClientErrorException.UnprocessableEntity e) {
			log.warn("ShoppingCart already exists for the current customer.");
		}
	}
}
