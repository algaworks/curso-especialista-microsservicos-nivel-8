package com.algaworks.algashop.ecommerce.application.client;

import com.algaworks.algashop.ecommerce.application.model.client.OrderModel;
import com.algaworks.algashop.ecommerce.application.model.client.OrderModelPage;
import com.algaworks.algashop.ecommerce.application.model.filter.OrderFilter;
import com.algaworks.algashop.ecommerce.application.properties.EcommerceProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Component
public class OrderClient {

	private final EcommerceProperties properties;
	private final RestClient userAuthenticatedRestClient;

	public OrderClient(EcommerceProperties properties,
					   @Qualifier("userAuthenticatedRestClient") RestClient userAuthenticatedRestClient) {
		this.properties = properties;
		this.userAuthenticatedRestClient = userAuthenticatedRestClient;
	}

	public OrderModelPage getOrders(OrderFilter orderFilter) {
		var builder = UriComponentsBuilder.fromUriString(properties.getApiUrl() + "/api/v1/customers/me/orders");

		if (orderFilter.getStatus() != null) {
			builder.queryParam("status", orderFilter.getStatus());
		}

		if (orderFilter.getPlacedAtFrom() != null) {
			builder.queryParam("placedAtFrom", orderFilter.getPlacedAtFrom());
		}

		if (orderFilter.getPlacedAtTo() != null) {
			builder.queryParam("placedAtTo", orderFilter.getPlacedAtTo());
		}

		if (orderFilter.getTotalAmountFrom() != null) {
			builder.queryParam("totalAmountFrom", orderFilter.getTotalAmountFrom());
		}

		if (orderFilter.getTotalAmountTo() != null) {
			builder.queryParam("totalAmountTo", orderFilter.getTotalAmountTo());
		}

		if (orderFilter.getOrderId() != null) {
			builder.queryParam("orderId", orderFilter.getOrderId());
		}

		if (orderFilter.getSize() > 0) {
			builder.queryParam("size", orderFilter.getSize());
		}

		if (orderFilter.getPage() >= 0) {
			builder.queryParam("page", orderFilter.getPage());
		}

		if (orderFilter.getSortDirection() != null) {
			builder.queryParam("sortDirection", orderFilter.getSortDirection());
		}

		if (orderFilter.getSort() != null) {
			builder.queryParam("sortByProperty", orderFilter.getSort());
		}

		URI uri = builder.build().toUri();

		var responseEntity = userAuthenticatedRestClient.get()
				.uri(uri)
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.toEntity(OrderModelPage.class);

		return responseEntity.getBody();
	}

	public OrderModel getOrder(String orderCode) {
		var responseEntity = userAuthenticatedRestClient.get()
				.uri(URI.create(properties.getApiUrl() + "/api/v1/customers/me/orders/" + orderCode))
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.toEntity(OrderModel.class);

		return responseEntity.getBody();
	}
}
