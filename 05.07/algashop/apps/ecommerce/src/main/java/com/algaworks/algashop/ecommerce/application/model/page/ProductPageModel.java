package com.algaworks.algashop.ecommerce.application.model.page;

import com.algaworks.algashop.ecommerce.application.model.client.CategoryModel;
import com.algaworks.algashop.ecommerce.application.model.client.ImageModel;
import com.algaworks.algashop.ecommerce.application.model.client.ProductModel;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class ProductPageModel {

	@Builder.Default
	private List<CategoryModel> categories = new ArrayList<>();

	private Boolean addedToCart;

	private ProductModel product;

	@Builder.Default
	private List<ImageModel> images = new ArrayList<>();

	public ModelAndView toModelAndView() {
		ModelAndView modelAndView = new ModelAndView("product");

		modelAndView.addObject("categories", categories);
		modelAndView.addObject("product", product);
		modelAndView.addObject("addedToCart", addedToCart);
		modelAndView.addObject("images", images);

		return modelAndView;
	}
}
