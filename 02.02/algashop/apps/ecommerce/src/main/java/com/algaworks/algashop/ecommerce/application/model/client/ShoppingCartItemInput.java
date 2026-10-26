package com.algaworks.algashop.ecommerce.application.model.client;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ShoppingCartItemInput {
	private String productId;
	private int quantity;
}
