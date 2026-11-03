package com.algaworks.algashop.ecommerce.application.model.page;

import com.algaworks.algashop.ecommerce.application.model.client.PageLinkModel;
import com.algaworks.algashop.ecommerce.application.model.client.PageModel;
import com.algaworks.algashop.ecommerce.application.model.client.ProductModel;
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
public class ProductCatalogPageModel extends PageModel<ProductModel> {
	@Builder.Default
	private List<ProductModel> products = new ArrayList<>();

	@Builder.Default
	private ProductFilter productFilter = new ProductFilter();

	@Builder.Default
	private List<PageLinkModel> pageLinks = new ArrayList<>();

	public ModelAndView toModelAndView() {
		ModelAndView modelAndView = new ModelAndView("products-catalog");
		modelAndView.addObject("products", products);
		modelAndView.addObject("productFilter", productFilter);
		modelAndView.addObject("pageLinks", pageLinks);
		return modelAndView;
	}
}
