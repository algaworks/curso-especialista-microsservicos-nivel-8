package com.algaworks.algashop.ecommerce.application.service;

import com.algaworks.algashop.ecommerce.application.client.ShoppingCartClient;
import com.algaworks.algashop.ecommerce.application.model.client.ShoppingCartItemInput;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShoppingCartServiceTest {

	@Mock
	private ShoppingCartClient shoppingCartClient;

	@Test
	void shouldAddItemWithoutCreatingShoppingCartWhenShoppingCartExists() {
		ShoppingCartService shoppingCartService = new ShoppingCartService(shoppingCartClient);
		ShoppingCartItemInput input = new ShoppingCartItemInput("product-1", 2);

		shoppingCartService.addItem(input);

		verify(shoppingCartClient).addItem(input);
		verify(shoppingCartClient, never()).createCurrentShoppingCart();
	}

	@Test
	void shouldCreateShoppingCartAndRetryAddItemWhenShoppingCartDoesNotExist() {
		ShoppingCartService shoppingCartService = new ShoppingCartService(shoppingCartClient);
		ShoppingCartItemInput input = new ShoppingCartItemInput("product-1", 2);

		doThrow(notFound())
				.doNothing()
				.when(shoppingCartClient).addItem(input);

		shoppingCartService.addItem(input);

		verify(shoppingCartClient).createCurrentShoppingCart();
		verify(shoppingCartClient, times(2)).addItem(input);
	}

	@Test
	void shouldRetryAddItemWhenShoppingCartIsCreatedByConcurrentRequest() {
		ShoppingCartService shoppingCartService = new ShoppingCartService(shoppingCartClient);
		ShoppingCartItemInput input = new ShoppingCartItemInput("product-1", 2);

		doThrow(notFound())
				.doNothing()
				.when(shoppingCartClient).addItem(input);
		doThrow(unprocessableEntity()).when(shoppingCartClient).createCurrentShoppingCart();

		shoppingCartService.addItem(input);

		verify(shoppingCartClient).createCurrentShoppingCart();
		verify(shoppingCartClient, times(2)).addItem(input);
	}

	@Test
	void shouldPropagateNonNotFoundErrorsWhenAddingItem() {
		ShoppingCartService shoppingCartService = new ShoppingCartService(shoppingCartClient);
		ShoppingCartItemInput input = new ShoppingCartItemInput("product-1", 2);

		doThrow(badRequest()).when(shoppingCartClient).addItem(input);

		assertThatExceptionOfType(HttpClientErrorException.BadRequest.class)
				.isThrownBy(() -> shoppingCartService.addItem(input));

		verify(shoppingCartClient, never()).createCurrentShoppingCart();
	}

	private HttpClientErrorException notFound() {
		return HttpClientErrorException.NotFound.create(
				HttpStatus.NOT_FOUND,
				"Not Found",
				HttpHeaders.EMPTY,
				new byte[0],
				StandardCharsets.UTF_8
		);
	}

	private HttpClientErrorException unprocessableEntity() {
		return HttpClientErrorException.UnprocessableEntity.create(
				HttpStatus.UNPROCESSABLE_ENTITY,
				"Unprocessable Entity",
				HttpHeaders.EMPTY,
				new byte[0],
				StandardCharsets.UTF_8
		);
	}

	private HttpClientErrorException badRequest() {
		return HttpClientErrorException.BadRequest.create(
				HttpStatus.BAD_REQUEST,
				"Bad Request",
				HttpHeaders.EMPTY,
				new byte[0],
				StandardCharsets.UTF_8
		);
	}
}
