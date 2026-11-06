package com.algaworks.algashop.ecommerce.application.model.client;

import io.micrometer.common.util.StringUtils;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddressModel {
	
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

	public String getAddressLine() {
		if (StringUtils.isNotBlank(complement)) {
			return String.format("%s %s %s, %s, %s - %s", street, number, complement, neighborhood, city, state);
		} else {
			return String.format("%s %s, %s, %s - %s", street, number, neighborhood, city, state);
		}
	}
}
