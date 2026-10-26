package com.algaworks.algashop.ecommerce.application.model.page;

import com.algaworks.algashop.ecommerce.application.model.client.CreditCardModel;
import com.algaworks.algashop.ecommerce.application.model.form.CheckoutForm;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Data
@Builder
public class CheckoutPageModel {

	@Builder.Default
	private CheckoutForm checkoutForm = new CheckoutForm();

	@Builder.Default
	private List<CreditCardModel> creditCards = List.of();

	private AlertMessage alertMessage;

	private String paymentProviderCreditCardTokenUrl;

	private String paymentProviderPublicKey;

	public ModelAndView toModelAndView() {
		ModelAndView modelAndView = new ModelAndView("checkout");

		modelAndView.addObject("checkoutForm", checkoutForm);
		modelAndView.addObject("creditCards", creditCards);
		modelAndView.addObject("alertMessage", alertMessage);
		modelAndView.addObject("paymentProviderCreditCardTokenUrl", paymentProviderCreditCardTokenUrl);
		modelAndView.addObject("paymentProviderPublicKey", paymentProviderPublicKey);

		return modelAndView;
	}
}
