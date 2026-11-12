package com.algaworks.algashop.ecommerce.application.controller;

import com.algaworks.algashop.ecommerce.application.client.CategoryClient;
import com.algaworks.algashop.ecommerce.application.client.HomeClient;
import com.algaworks.algashop.ecommerce.application.client.ProductClient;
import com.algaworks.algashop.ecommerce.application.model.client.CategoryModel;
import com.algaworks.algashop.ecommerce.application.model.client.HomeApiModel;
import com.algaworks.algashop.ecommerce.application.model.client.PageModel;
import com.algaworks.algashop.ecommerce.application.model.client.ProductModel;
import com.algaworks.algashop.ecommerce.application.model.filter.ProductFilter;
import com.algaworks.algashop.ecommerce.application.properties.EcommerceProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HomeControllerTest {

	@Mock
	private ProductClient productClient;

	@Mock
	private CategoryClient categoryClient;

	@Mock
	private HomeClient homeClient;

	@Test
	void shouldUseLegacyClientsWhenHomeBffFeatureIsDisabled() {
		HomeController controller = controller(false);
		List<ProductModel> products = List.of(product("product-1"));
		List<CategoryModel> categories = List.of(
				category("category-1"),
				category("category-2"),
				category("category-3"),
				category("category-4")
		);

		when(productClient.findAll(any(ProductFilter.class))).thenReturn(productPage(products));
		when(categoryClient.findAll()).thenReturn(categoryPage(categories));

		ModelAndView modelAndView = controller.index();

		assertThat(modelAndView.getViewName()).isEqualTo("index");
		assertThat(modelAndView.getModel().get("products")).isEqualTo(products);
		assertThat(modelAndView.getModel().get("topCategories")).isEqualTo(categories.subList(0, 3));
		verify(homeClient, never()).findHome();
	}

	@Test
	void shouldUseHomeBffWhenFeatureIsEnabled() {
		HomeController controller = controller(true);
		List<ProductModel> highlights = List.of(product("product-1"));
		List<CategoryModel> categories = List.of(
				category("category-1"),
				category("category-2"),
				category("category-3"),
				category("category-4")
		);
		HomeApiModel home = new HomeApiModel();
		home.setHighlights(highlights);
		home.setCategories(categories);

		when(homeClient.findHome()).thenReturn(home);

		ModelAndView modelAndView = controller.index();

		assertThat(modelAndView.getViewName()).isEqualTo("index");
		assertThat(modelAndView.getModel().get("products")).isEqualTo(highlights);
		assertThat(modelAndView.getModel().get("topCategories")).isEqualTo(categories.subList(0, 3));
		verify(productClient, never()).findAll(any(ProductFilter.class));
		verify(categoryClient, never()).findAll();
	}

	@Test
	void shouldFallbackToLegacyClientsWhenHomeBffFails() {
		HomeController controller = controller(true);
		List<ProductModel> products = List.of(product("product-1"));
		List<CategoryModel> categories = List.of(category("category-1"));

		when(homeClient.findHome()).thenThrow(new RuntimeException("bff unavailable"));
		when(productClient.findAll(any(ProductFilter.class))).thenReturn(productPage(products));
		when(categoryClient.findAll()).thenReturn(categoryPage(categories));

		ModelAndView modelAndView = controller.index();

		assertThat(modelAndView.getViewName()).isEqualTo("index");
		assertThat(modelAndView.getModel().get("products")).isEqualTo(products);
		assertThat(modelAndView.getModel().get("topCategories")).isEqualTo(categories);
	}

	@Test
	void shouldKeepRenderingEmptyListsWhenLegacyClientsFail() {
		HomeController controller = controller(false);

		when(productClient.findAll(any(ProductFilter.class))).thenThrow(new RuntimeException("products unavailable"));
		when(categoryClient.findAll()).thenThrow(new RuntimeException("categories unavailable"));

		ModelAndView modelAndView = controller.index();

		assertThat(modelAndView.getViewName()).isEqualTo("index");
		assertThat(modelAndView.getModel().get("products")).isEqualTo(List.of());
		assertThat(modelAndView.getModel().get("topCategories")).isEqualTo(List.of());
	}

	private HomeController controller(boolean homeBffEnabled) {
		EcommerceProperties properties = new EcommerceProperties();
		properties.getFeatures().setHomeBffEnabled(homeBffEnabled);
		return new HomeController(productClient, categoryClient, homeClient, properties);
	}

	private PageModel<ProductModel> productPage(List<ProductModel> content) {
		return new PageModel<>(0, content.size(), 1, content.size(), content);
	}

	private PageModel<CategoryModel> categoryPage(List<CategoryModel> content) {
		return new PageModel<>(0, content.size(), 1, content.size(), content);
	}

	private ProductModel product(String id) {
		ProductModel product = new ProductModel();
		product.setId(id);
		return product;
	}

	private CategoryModel category(String id) {
		CategoryModel category = new CategoryModel();
		category.setId(id);
		return category;
	}
}
