package com.algaworks.algashop.ecommerce.application.model.form;

import com.algaworks.algashop.ecommerce.application.model.client.AddressModel;
import com.algaworks.algashop.ecommerce.application.model.client.CustomerModel;
import com.algaworks.algashop.ecommerce.application.model.client.PersonalInfoModel;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutForm {

	@NotNull
	@Builder.Default
	private PersonalInfoModel billingInfo = emptyPersonalInfo();

	@NotNull
	@Builder.Default
	private PersonalInfoModel shippingInfo = emptyPersonalInfo();

	private boolean billToDifferentAddress;
	
	@NotNull
	private PaymentMethod paymentMethod;

	private String creditCardId;

	public static CheckoutForm of(CustomerModel customerModel) {
		if (customerModel == null) {
			return new CheckoutForm();
		}
		AddressModel address = customerModel.getAddress();
		PersonalInfoModel personalInfo = PersonalInfoModel.builder()
				.fullName(customerModel.getFullName())
				.document(customerModel.getDocument())
				.phone(customerModel.getPhone())
				.address(AddressModel.builder()
						.street(address.getStreet())
						.number(address.getNumber())
						.complement(address.getComplement())
						.neighborhood(address.getNeighborhood())
						.city(address.getCity())
						.state(address.getState())
						.zipCode(address.getZipCode())
						.build())
				.build();
		return CheckoutForm.builder()
				.shippingInfo(personalInfo)
				.billingInfo(emptyPersonalInfo())
				.build();
	}

	private static PersonalInfoModel emptyPersonalInfo() {
		return PersonalInfoModel.builder()
				.address(AddressModel.builder().build())
				.build();
	}
}
