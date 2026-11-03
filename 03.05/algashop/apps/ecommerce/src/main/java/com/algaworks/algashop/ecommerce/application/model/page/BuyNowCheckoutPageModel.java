package com.algaworks.algashop.ecommerce.application.model.page;

import com.algaworks.algashop.ecommerce.application.model.client.CreditCardModel;
import com.algaworks.algashop.ecommerce.application.model.client.ProductModel;
import com.algaworks.algashop.ecommerce.application.model.form.BuyNowCheckoutForm;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class BuyNowCheckoutPageModel {

	@Builder.Default
	private BuyNowCheckoutForm buyNowCheckoutForm = new BuyNowCheckoutForm();

	@Builder.Default
	private List<CreditCardModel> creditCards = List.of();

	private ProductModel product;

	private BigDecimal unitPrice;

	private BigDecimal subtotal;

	private AlertMessage alertMessage;

	private String paymentProviderCreditCardTokenUrl;

	private String paymentProviderPublicKey;

	public ModelAndView toModelAndView() {
		ModelAndView modelAndView = new ModelAndView("buy-now");

		modelAndView.addObject("buyNowCheckoutForm", buyNowCheckoutForm);
		modelAndView.addObject("creditCards", creditCards);
		modelAndView.addObject("product", product);
		modelAndView.addObject("unitPrice", unitPrice);
		modelAndView.addObject("subtotal", subtotal);
		modelAndView.addObject("alertMessage", alertMessage);
		modelAndView.addObject("paymentProviderCreditCardTokenUrl", paymentProviderCreditCardTokenUrl);
		modelAndView.addObject("paymentProviderPublicKey", paymentProviderPublicKey);

		return modelAndView;
	}
}
