package com.algaworks.algashop.ecommerce.application.model.page;

import com.algaworks.algashop.ecommerce.application.model.client.CategoryModel;
import com.algaworks.algashop.ecommerce.application.model.client.PageModel;
import com.algaworks.algashop.ecommerce.application.model.filter.ProductFilter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryPageModel extends PageModel<CategoryModel> {
	@Builder.Default
	private List<CategoryModel> categories = new ArrayList<>();

	@Builder.Default
	private List<CategoryModel> topCategories = new ArrayList<>();

	@Builder.Default
	private ProductFilter productFilter = new ProductFilter();

	public ModelAndView toModelAndView() {
		ModelAndView modelAndView = new ModelAndView("categories");
		modelAndView.addObject("categories", categories);
		modelAndView.addObject("topCategories", topCategories);
		modelAndView.addObject("productFilter", productFilter);

		return modelAndView;
	}
}
