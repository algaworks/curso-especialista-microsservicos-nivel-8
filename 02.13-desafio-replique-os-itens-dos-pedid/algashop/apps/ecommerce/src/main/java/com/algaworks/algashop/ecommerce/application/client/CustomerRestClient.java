package com.algaworks.algashop.ecommerce.application.client;

import com.algaworks.algashop.ecommerce.application.model.client.CustomerInput;
import com.algaworks.algashop.ecommerce.application.model.client.CustomerModel;
import com.algaworks.algashop.ecommerce.application.model.client.CustomerUpdateInput;
import com.algaworks.algashop.ecommerce.application.properties.EcommerceProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.net.URI;

@Component
@Slf4j
public class CustomerRestClient {

	private static final String CUSTOMERS_ME_URI = "/api/v1/customers/me";

	private final EcommerceProperties properties;
	private final RestClient userAuthenticatedRestClient;

	public CustomerRestClient(EcommerceProperties properties,
							  @Qualifier("userAuthenticatedRestClient") RestClient userAuthenticatedRestClient) {
		this.properties = properties;
		this.userAuthenticatedRestClient = userAuthenticatedRestClient;
	}

	public CustomerModel createMyProfile(CustomerInput customerInput) {
		try {
			log.info("Create customer:\n{}\n", customerInput);
			ResponseEntity<CustomerModel> responseEntity = userAuthenticatedRestClient.post()
					.uri(URI.create(properties.getApiUrl() + CUSTOMERS_ME_URI))
					.body(customerInput)
					.accept(MediaType.APPLICATION_JSON)
					.contentType(MediaType.APPLICATION_JSON)
					.retrieve()
					.toEntity(CustomerModel.class);

			return responseEntity.getBody();
		} catch (RestClientResponseException e) {
			log.error("Error when tried to create Customer:\n{}\n", e.getResponseBodyAsString());
			throw e;
		}
	}

	public CustomerModel getMyProfile() {
		ResponseEntity<CustomerModel> responseEntity = userAuthenticatedRestClient.get()
				.uri(URI.create(properties.getApiUrl() + CUSTOMERS_ME_URI))
				.accept(MediaType.APPLICATION_JSON)
				.header("Cache-Control", "no-cache")
				.retrieve()
				.toEntity(CustomerModel.class);

		return responseEntity.getBody();
	}

	public CustomerModel updateMyProfile(CustomerUpdateInput customerInput) {
		ResponseEntity<CustomerModel> responseEntity = userAuthenticatedRestClient.put()
				.uri(URI.create(properties.getApiUrl() + CUSTOMERS_ME_URI))
				.body(customerInput)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.retrieve()
				.toEntity(CustomerModel.class);

		return responseEntity.getBody();
	}
}
