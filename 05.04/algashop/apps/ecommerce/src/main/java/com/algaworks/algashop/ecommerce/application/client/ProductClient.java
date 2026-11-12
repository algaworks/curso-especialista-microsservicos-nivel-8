package com.algaworks.algashop.ecommerce.application.client;

import com.algaworks.algashop.ecommerce.application.model.client.ImageModel;
import com.algaworks.algashop.ecommerce.application.model.client.PageModel;
import com.algaworks.algashop.ecommerce.application.model.client.ProductModel;
import com.algaworks.algashop.ecommerce.application.model.client.ProductModelPage;
import com.algaworks.algashop.ecommerce.application.model.filter.ProductFilter;
import com.algaworks.algashop.ecommerce.application.properties.EcommerceProperties;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
@Component
public class ProductClient {

	private final EcommerceProperties properties;
	private final RestClient restClient;


	public PageModel<ProductModel> findAll(ProductFilter productFilter) {
		URI uri = UriComponentsBuilder.fromUriString(properties.getApiUrl() + "/api/v1/products")
				.queryParams(productFilter.toQueryParams())
				.build()
				.toUri();
		
		ResponseEntity<ProductModelPage> responseEntity = restClient.get()
				.uri(uri)
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.toEntity(ProductModelPage.class);

		return responseEntity.getBody();
	}

	public ProductModel findById(String id) {
		ResponseEntity<ProductModel> responseEntity = restClient.get()
				.uri(URI.create(properties.getApiUrl() + "/api/v1/products/" + id))
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.toEntity(ProductModel.class);

		return responseEntity.getBody();
	}

	public List<ImageModel> findImagesByProductId(String id) {
		ResponseEntity<ImageModel[]> responseEntity = restClient.get()
				.uri(URI.create(properties.getApiUrl() + "/api/v1/products/" + id + "/images"))
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.toEntity(ImageModel[].class);

		ImageModel[] body = responseEntity.getBody();
		return body != null ? Arrays.asList(body) : List.of();
	}
}
