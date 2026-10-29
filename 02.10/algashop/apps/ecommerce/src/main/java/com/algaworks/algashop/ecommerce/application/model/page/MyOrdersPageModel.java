package com.algaworks.algashop.ecommerce.application.model.page;

import com.algaworks.algashop.ecommerce.application.model.client.OrderModelPage;
import com.algaworks.algashop.ecommerce.application.model.client.PageLinkModel;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class MyOrdersPageModel {

	@Builder.Default
	private OrderModelPage ordersPage = new OrderModelPage();

	@Builder.Default
	private List<PageLinkModel> pageLinks = new ArrayList<>();

	public ModelAndView toModelAndView() {
		ModelAndView modelAndView = new ModelAndView("myorders");

		modelAndView.addObject("ordersPage", ordersPage);
		modelAndView.addObject("pageNavigation", true);
		modelAndView.addObject("pageLinks", pageLinks);

		return modelAndView;
	}
}
