package com.algaworks.algashop.ecommerce.application.model.page;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.servlet.ModelAndView;

@Data
@Builder
public class ShoppingCartPageModel {

	private Boolean removed;

	public ModelAndView toModelAndView() {
		ModelAndView modelAndView = new ModelAndView("shoppingcart");

		modelAndView.addObject("removed", removed);

		return modelAndView;
	}
}
