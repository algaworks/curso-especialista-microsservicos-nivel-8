package com.algaworks.algashop.ecommerce.application.service;

import com.algaworks.algashop.ecommerce.application.client.CategoryClient;
import com.algaworks.algashop.ecommerce.application.model.client.CategoryModel;
import com.algaworks.algashop.ecommerce.application.model.client.ShoppingCartModel;
import com.algaworks.algashop.ecommerce.infraestructure.security.AlgaShopSecurityService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class MenuService {

	private final AlgaShopSecurityService algaShopSecurityService;
	private final CategoryClient categoryClient;
	private final ShoppingCartService shoppingCartService;

	public ShoppingCartModel loadShoppingCart() {
		try {
			if (algaShopSecurityService.getAuthentication().isPresent()) {
				return shoppingCartService.findCurrentShoppingCart();
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return new ShoppingCartModel();
	}

	public List<CategoryModel> loadCategories() {
		try {
			return categoryClient.findAll().getContent();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return new ArrayList<>();
	}

}
