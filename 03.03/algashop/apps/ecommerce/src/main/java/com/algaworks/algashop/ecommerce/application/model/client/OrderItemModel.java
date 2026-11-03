package com.algaworks.algashop.ecommerce.application.model.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemModel {
	private String productId;
	private String productName;
	private BigDecimal price;
	private Integer quantity;
	private BigDecimal totalAmount;

	public String getName() {
		return productName;
	}
}
