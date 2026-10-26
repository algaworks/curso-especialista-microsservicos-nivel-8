package com.algaworks.algashop.ecommerce.application.model.client;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class HomeApiModel {

	private List<CategoryModel> categories = new ArrayList<>();
	private List<ProductModel> highlights = new ArrayList<>();

}
