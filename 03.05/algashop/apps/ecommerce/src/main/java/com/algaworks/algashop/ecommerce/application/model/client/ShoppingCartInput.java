package com.algaworks.algashop.ecommerce.application.model.client;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingCartInput {
	@NotNull
	private String customerId;
}
