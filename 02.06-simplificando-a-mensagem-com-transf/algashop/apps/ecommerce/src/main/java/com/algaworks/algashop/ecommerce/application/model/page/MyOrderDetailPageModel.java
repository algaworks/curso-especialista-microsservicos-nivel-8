package com.algaworks.algashop.ecommerce.application.model.page;

import com.algaworks.algashop.ecommerce.application.model.client.CategoryModel;
import com.algaworks.algashop.ecommerce.application.model.client.OrderModel;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class MyOrderDetailPageModel {

	@Builder.Default
	private List<CategoryModel> categories = new ArrayList<>();

	@Builder.Default
	private String viewName = "myorders-detail";

	private OrderModel order;

	private String orderCode;

	private AlertMessage alertMessage;

	private String loadingMessage;

	private String currentOrderStatus;

	private boolean autoRefresh;

	public ModelAndView toModelAndView() {
		ModelAndView modelAndView = new ModelAndView(viewName);

		modelAndView.addObject("categories", categories);
		modelAndView.addObject("order", order);
		modelAndView.addObject("orderCode", orderCode);
		modelAndView.addObject("alertMessage", alertMessage);
		modelAndView.addObject("loadingMessage", loadingMessage);
		modelAndView.addObject("currentOrderStatus", currentOrderStatus);
		modelAndView.addObject("autoRefresh", autoRefresh);

		return modelAndView;
	}
}
