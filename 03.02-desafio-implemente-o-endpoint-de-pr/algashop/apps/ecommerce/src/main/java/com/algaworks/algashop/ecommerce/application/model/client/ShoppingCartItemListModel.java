package com.algaworks.algashop.ecommerce.application.model.client;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ShoppingCartItemListModel {
	private List<ShoppingCartItemModel> items = new ArrayList<>();
}
