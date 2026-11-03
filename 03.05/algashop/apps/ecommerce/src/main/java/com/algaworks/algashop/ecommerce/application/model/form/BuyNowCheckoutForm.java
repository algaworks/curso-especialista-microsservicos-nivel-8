package com.algaworks.algashop.ecommerce.application.model.form;

import com.algaworks.algashop.ecommerce.application.model.client.CustomerModel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BuyNowCheckoutForm extends CheckoutForm {

	@NotBlank
	private String productId;

	@NotNull
	@Positive
	private Integer quantity;

	public static BuyNowCheckoutForm of(CustomerModel customerModel, String productId, Integer quantity) {
		CheckoutForm checkoutForm = CheckoutForm.of(customerModel);
		BuyNowCheckoutForm buyNowCheckoutForm = new BuyNowCheckoutForm();
		buyNowCheckoutForm.setShippingInfo(checkoutForm.getShippingInfo());
		buyNowCheckoutForm.setBillingInfo(checkoutForm.getBillingInfo());
		buyNowCheckoutForm.setProductId(productId);
		buyNowCheckoutForm.setQuantity(quantity);
		return buyNowCheckoutForm;
	}

}
