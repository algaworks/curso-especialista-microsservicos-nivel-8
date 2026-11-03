package com.algaworks.algashop.ecommerce.application.client;

import com.algaworks.algashop.ecommerce.application.model.client.BuyNowCheckoutModel;
import com.algaworks.algashop.ecommerce.application.model.client.CheckoutModel;
import com.algaworks.algashop.ecommerce.application.model.client.OrderModel;
import com.algaworks.algashop.ecommerce.application.properties.EcommerceProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.net.URI;

@Component
public class CheckoutClient {

	private final EcommerceProperties properties;
	private final RestClient userAuthenticatedRestClient;

	public CheckoutClient(EcommerceProperties properties,
						  @Qualifier("userAuthenticatedRestClient") RestClient userAuthenticatedRestClient) {
		this.properties = properties;
		this.userAuthenticatedRestClient = userAuthenticatedRestClient;
	}

	public OrderModel checkout(CheckoutModel checkoutInput) {
		ResponseEntity<OrderModel> responseEntity = userAuthenticatedRestClient.post()
				.uri(URI.create(properties.getApiUrl() + "/api/v1/customers/me/orders"))
				.body(checkoutInput)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.valueOf("application/vnd.order-with-shopping-cart.v1+json"))
				.retrieve()
				.toEntity(OrderModel.class);

		return responseEntity.getBody();
	}

	public OrderModel buyNow(BuyNowCheckoutModel checkoutInput) {
		ResponseEntity<OrderModel> responseEntity = userAuthenticatedRestClient.post()
				.uri(URI.create(properties.getApiUrl() + "/api/v1/customers/me/orders"))
				.body(checkoutInput)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.valueOf("application/vnd.order-with-product.v1+json"))
				.retrieve()
				.toEntity(OrderModel.class);

		return responseEntity.getBody();
	}

}
