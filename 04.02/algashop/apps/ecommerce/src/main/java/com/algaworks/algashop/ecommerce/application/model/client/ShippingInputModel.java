package com.algaworks.algashop.ecommerce.application.model.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShippingInputModel {
	private RecipientModel recipient;
	private AddressModel address;
}
