package com.algaworks.algashop.ecommerce.application.client;

import com.algaworks.algashop.ecommerce.application.model.client.HomeApiModel;
import com.algaworks.algashop.ecommerce.application.properties.EcommerceProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.net.URI;

@Component
@RequiredArgsConstructor
public class HomeClient {

	private final EcommerceProperties properties;
	private final RestClient restClient;

	public HomeApiModel findHome() {
		ResponseEntity<HomeApiModel> responseEntity = restClient.get()
				.uri(URI.create(properties.getApiUrl() + "/api/v1/ecommerce/home"))
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.toEntity(HomeApiModel.class);

		return responseEntity.getBody();
	}

}
