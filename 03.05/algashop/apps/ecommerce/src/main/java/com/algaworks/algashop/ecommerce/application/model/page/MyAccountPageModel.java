package com.algaworks.algashop.ecommerce.application.model.page;

import com.algaworks.algashop.ecommerce.application.model.client.CategoryModel;
import com.algaworks.algashop.ecommerce.application.model.client.OrderModelPage;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class MyAccountPageModel {

	@Builder.Default
	private List<CategoryModel> categories = new ArrayList<>();

	@Builder.Default
	private OrderModelPage ordersPage = new OrderModelPage();

	public ModelAndView toModelAndView() {
		ModelAndView modelAndView = new ModelAndView("myaccount");

		modelAndView.addObject("categories", categories);
		modelAndView.addObject("ordersPage", ordersPage);
		modelAndView.addObject("pageNavigation", false);

		return modelAndView;
	}
}
