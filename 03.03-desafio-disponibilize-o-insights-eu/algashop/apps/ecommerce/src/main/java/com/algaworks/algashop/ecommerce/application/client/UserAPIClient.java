package com.algaworks.algashop.ecommerce.application.client;

import com.algaworks.algashop.ecommerce.application.model.client.AuthUserInput;
import com.algaworks.algashop.ecommerce.application.properties.EcommerceProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.net.URI;

@Component
public class UserAPIClient {

	private final EcommerceProperties properties;
	private final RestClient restClient;
	private final RestClient userAuthenticatedRestClient;

	public UserAPIClient(EcommerceProperties properties,
						 @Qualifier("restClient") RestClient restClient,
						 @Qualifier("userAuthenticatedRestClient") RestClient userAuthenticatedRestClient) {
		this.properties = properties;
		this.restClient = restClient;
		this.userAuthenticatedRestClient = userAuthenticatedRestClient;
	}

	public void create(AuthUserInput input) {
		restClient.post()
				.uri(URI.create(properties.getAuthorizationServerUrl() + "/api/v1/users"))
				.body(input)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.retrieve()
				.toEntity(Void.class);
	}

	public void requestMyPasswordChange() {
		userAuthenticatedRestClient.post()
				.uri(URI.create(properties.getAuthorizationServerUrl() + "/api/v1/users/me/password-change"))
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.toEntity(Void.class);
	}

	public void deleteMe() {
		userAuthenticatedRestClient.delete()
				.uri(URI.create(properties.getAuthorizationServerUrl() + "/api/v1/users/me"))
				.retrieve()
				.toEntity(Void.class);
	}
}
