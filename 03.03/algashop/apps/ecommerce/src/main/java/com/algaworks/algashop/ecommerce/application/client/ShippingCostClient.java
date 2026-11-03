package com.algaworks.algashop.ecommerce.application.client;

import com.algaworks.algashop.ecommerce.application.model.client.ShippingCostPreviewInput;
import com.algaworks.algashop.ecommerce.application.model.client.ShippingCostPreviewModel;
import com.algaworks.algashop.ecommerce.application.properties.EcommerceProperties;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.net.URI;

@Component
public class ShippingCostClient {

	private final EcommerceProperties properties;
	private final RestClient userAuthenticatedRestClient;

	public ShippingCostClient(EcommerceProperties properties,
							  RestClient restClient) {
		this.properties = properties;
		this.userAuthenticatedRestClient = restClient;
	}

	public ShippingCostPreviewModel preview(ShippingCostPreviewInput input) {
		ResponseEntity<ShippingCostPreviewModel> responseEntity = userAuthenticatedRestClient.post()
				.uri(URI.create(properties.getApiUrl() + "/api/v1/shipping-cost-previews"))
				.body(input)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.retrieve()
				.toEntity(ShippingCostPreviewModel.class);

		return responseEntity.getBody();
	}

}
