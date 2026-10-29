package com.algaworks.algashop.ecommerce.application.model.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BillingModel {
	private String firstName;
	private String lastName;
	private String document;
	private String email;
	private String phone;
	private AddressModel address;
}
