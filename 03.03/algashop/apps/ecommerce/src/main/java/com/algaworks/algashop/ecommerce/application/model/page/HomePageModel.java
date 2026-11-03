package com.algaworks.algashop.ecommerce.application.model.page;

import com.algaworks.algashop.ecommerce.application.model.client.CategoryModel;
import com.algaworks.algashop.ecommerce.application.model.client.ProductModel;
import com.algaworks.algashop.ecommerce.application.model.filter.ProductFilter;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class HomePageModel {

	@Builder.Default
	private List<ProductModel> products = new ArrayList<>();

	@Builder.Default
	private List<CategoryModel> topCategories = new ArrayList<>();

	@Builder.Default
	private ProductFilter productFilter = new ProductFilter();

	public ModelAndView toModelAndView() {
		ModelAndView modelAndView = new ModelAndView("index");
		modelAndView.addObject("productFilter", productFilter);
		modelAndView.addObject("products", products);
		modelAndView.addObject("topCategories", topCategories);
		return modelAndView;
	}
}
