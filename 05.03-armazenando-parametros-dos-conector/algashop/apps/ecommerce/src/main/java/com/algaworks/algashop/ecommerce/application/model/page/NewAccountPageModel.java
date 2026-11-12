package com.algaworks.algashop.ecommerce.application.model.page;

import com.algaworks.algashop.ecommerce.application.model.client.CategoryModel;
import com.algaworks.algashop.ecommerce.application.model.form.NewCustomerForm;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class NewAccountPageModel {

	@Builder.Default
	private List<CategoryModel> categories = new ArrayList<>();

	private boolean newAccountCreated;

	@Builder.Default
	private NewCustomerForm newCustomerForm = new NewCustomerForm();

	public ModelAndView toModelAndView() {
		ModelAndView modelAndView = new ModelAndView("newaccount");

		modelAndView.addObject("categories", categories);
		modelAndView.addObject("newAccountCreated", newAccountCreated);
		modelAndView.addObject("newCustomerForm", newCustomerForm);

		return modelAndView;
	}
}
