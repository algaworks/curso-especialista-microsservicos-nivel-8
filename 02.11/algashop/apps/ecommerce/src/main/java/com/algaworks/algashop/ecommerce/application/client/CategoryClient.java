package com.algaworks.algashop.ecommerce.application.client;

import com.algaworks.algashop.ecommerce.application.model.client.CategoryModel;
import com.algaworks.algashop.ecommerce.application.model.client.PageModel;
import com.algaworks.algashop.ecommerce.application.model.page.CategoryPageModel;
import com.algaworks.algashop.ecommerce.application.properties.EcommerceProperties;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.net.URI;

@AllArgsConstructor
@Component
public class CategoryClient {

	private final EcommerceProperties properties;
	private final RestClient restClient;

	public PageModel<CategoryModel> findAll() {
		ResponseEntity<CategoryPageModel> responseEntity = restClient.get()
				.uri(URI.create(properties.getApiUrl() + "/api/v1/categories"))
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.toEntity(CategoryPageModel.class);

		return responseEntity.getBody();
	}

}
