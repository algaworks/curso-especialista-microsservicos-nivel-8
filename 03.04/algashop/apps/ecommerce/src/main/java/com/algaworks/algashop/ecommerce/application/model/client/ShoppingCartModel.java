package com.algaworks.algashop.ecommerce.application.model.client;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class ShoppingCartModel {
	private String id;
	private String customerId;
	private Integer totalItems = 0;
	private BigDecimal totalAmount = BigDecimal.ZERO;
	private List<ShoppingCartItemModel> items = new ArrayList<>();

	@JsonIgnore
	public boolean containsUnavailableItems() {
		return items != null && items.stream().anyMatch(ShoppingCartItemModel::isUnavailable);
	}
}
