package com.algaworks.algashop.ecommerce.application.model.page;

import com.algaworks.algashop.ecommerce.application.model.client.CategoryModel;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class NewCustomerPageModel {

	@Builder.Default
	private List<CategoryModel> categories = new ArrayList<>();

	public ModelAndView toModelAndView() {
		ModelAndView modelAndView = new ModelAndView("newcustomer");

		modelAndView.addObject("categories", categories);

		return modelAndView;
	}
}
