package com.algaworks.algashop.ecommerce.application.model.client;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerUpdateInput {

	@NotBlank
	private String firstName;

	@NotBlank
	private String lastName;

	@NotBlank
	private String phone;

	private boolean promotionNotificationsAllowed;

	@Valid
	@NotNull
	private AddressModel address;
}
