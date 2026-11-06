package com.algaworks.algashop.ecommerce.application.client;

import com.algaworks.algashop.ecommerce.application.model.client.CreditCardModel;
import com.algaworks.algashop.ecommerce.application.model.client.TokenizedCreditCardInput;
import com.algaworks.algashop.ecommerce.application.properties.EcommerceProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.util.List;

@Component
public class CreditCardClient {

	private final EcommerceProperties properties;
	private final RestClient userAuthenticatedRestClient;

	public CreditCardClient(EcommerceProperties properties,
							@Qualifier("userAuthenticatedRestClient") RestClient userAuthenticatedRestClient) {
		this.properties = properties;
		this.userAuthenticatedRestClient = userAuthenticatedRestClient;
	}

	public List<CreditCardModel> findAll() {
		ResponseEntity<List<CreditCardModel>> responseEntity = userAuthenticatedRestClient.get()
				.uri(URI.create(creditCardsUri()))
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.toEntity(new ParameterizedTypeReference<>() {
				});

		return responseEntity.getBody();
	}

	public CreditCardModel findById(String creditCardId) {
		ResponseEntity<CreditCardModel> responseEntity = userAuthenticatedRestClient.get()
				.uri(URI.create(creditCardsUri() + "/" + creditCardId))
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.toEntity(CreditCardModel.class);

		return responseEntity.getBody();
	}

	public CreditCardModel register(TokenizedCreditCardInput input) {
		ResponseEntity<CreditCardModel> responseEntity = userAuthenticatedRestClient.post()
				.uri(URI.create(creditCardsUri()))
				.body(input)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.retrieve()
				.toEntity(CreditCardModel.class);

		return responseEntity.getBody();
	}

	public void deleteById(String creditCardId) {
		userAuthenticatedRestClient.delete()
				.uri(URI.create(creditCardsUri() + "/" + creditCardId))
				.retrieve()
				.toBodilessEntity();
	}

	private String creditCardsUri() {
		return properties.getApiUrl() + "/api/v1/customers/me/credit-cards";
	}
}
