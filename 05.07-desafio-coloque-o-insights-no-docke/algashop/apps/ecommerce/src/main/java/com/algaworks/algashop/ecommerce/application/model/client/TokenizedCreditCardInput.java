package com.algaworks.algashop.ecommerce.application.model.client;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenizedCreditCardInput {
	@NotBlank
	private String tokenizedCard;
}
