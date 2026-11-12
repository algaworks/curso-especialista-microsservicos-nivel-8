package com.algaworks.algashop.ecommerce.application.controller;

import com.algaworks.algashop.ecommerce.application.client.CategoryClient;
import com.algaworks.algashop.ecommerce.application.client.HomeClient;
import com.algaworks.algashop.ecommerce.application.client.ProductClient;
import com.algaworks.algashop.ecommerce.application.model.client.CategoryModel;
import com.algaworks.algashop.ecommerce.application.model.client.HomeApiModel;
import com.algaworks.algashop.ecommerce.application.model.client.PageModel;
import com.algaworks.algashop.ecommerce.application.model.client.ProductModel;
import com.algaworks.algashop.ecommerce.application.model.filter.ProductFilter;
import com.algaworks.algashop.ecommerce.application.model.page.HomePageModel;
import com.algaworks.algashop.ecommerce.application.properties.EcommerceProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Objects;

@Controller
@RequiredArgsConstructor
@Slf4j
public class HomeController {

	private final ProductClient productClient;
	private final CategoryClient categoryClient;
	private final HomeClient homeClient;
	private final EcommerceProperties properties;

	@GetMapping("/")
	public ModelAndView index() {
		if (properties.getFeatures().isHomeBffEnabled()) {
			try {
				return buildHomeFromBff().toModelAndView();
			} catch (Exception _) {
				log.warn("Failed to fetch home from BFF, falling back to legacy clients");
			}
		}

		return buildHomeFromLegacyClients().toModelAndView();
	}

	private HomePageModel buildHomeFromBff() {
		HomeApiModel home = homeClient.findHome();

		return HomePageModel.builder()
				.products(Objects.requireNonNullElse(home.getHighlights(), List.of()))
				.topCategories(firstThree(Objects.requireNonNullElse(home.getCategories(), List.of())))
				.build();
	}

	private HomePageModel buildHomeFromLegacyClients() {
		var pageBuilder = HomePageModel.builder();

		try {
			PageModel<ProductModel> products = productClient.findAll(new ProductFilter());
			pageBuilder.products(products.getContent());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		try {
			PageModel<CategoryModel> categories = categoryClient.findAll();
			List<CategoryModel> topCategories = categories.getContent();
			if (categories.getTotalElements() > 3) {
				topCategories = categories.getContent().subList(0,3);
			}
			pageBuilder.topCategories(topCategories);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return pageBuilder.build();
	}

	private List<CategoryModel> firstThree(List<CategoryModel> categories) {
		if (categories.size() > 3) {
			return categories.subList(0, 3);
		}

		return categories;
	}
}
