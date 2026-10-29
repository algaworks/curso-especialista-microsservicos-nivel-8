package com.algaworks.algashop.ecommerce.application.model.client;

import lombok.Data;

@Data
public class CategoryModel {
	private String id;
	private String name;
	private boolean enabled;
	private String slug;
}
