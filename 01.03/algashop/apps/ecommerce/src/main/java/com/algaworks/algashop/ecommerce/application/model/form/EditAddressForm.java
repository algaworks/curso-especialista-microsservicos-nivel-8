package com.algaworks.algashop.ecommerce.application.model.form;

import com.algaworks.algashop.ecommerce.application.model.client.AddressModel;
import com.algaworks.algashop.ecommerce.application.model.client.CustomerModel;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EditAddressForm {
	
	@NotBlank
	private String street;

	@NotBlank
	private String number;

	private String complement;

	@NotBlank
	private String neighborhood;

	@NotBlank
	private String city;

	@NotBlank
	private String state;

	@NotBlank
	private String zipCode;

	public static EditAddressForm of(CustomerModel customerModel) {
		if (customerModel == null) {
			return EditAddressForm.builder().build();
		}
		AddressModel address = customerModel.getAddress();
		return EditAddressForm.builder()
				.street(address.getStreet())
				.number(address.getNumber())
				.complement(address.getComplement())
				.neighborhood(address.getNeighborhood())
				.city(address.getCity())
				.state(address.getState())
				.zipCode(address.getZipCode())
				.build();
	}

	public AddressModel toAddress() {
		return AddressModel.builder()
				.street(getStreet())
				.number(getNumber())
				.complement(getComplement())
				.neighborhood(getNeighborhood())
				.city(getCity())
				.state(getState())
				.zipCode(getZipCode())
				.build();
	}
}
