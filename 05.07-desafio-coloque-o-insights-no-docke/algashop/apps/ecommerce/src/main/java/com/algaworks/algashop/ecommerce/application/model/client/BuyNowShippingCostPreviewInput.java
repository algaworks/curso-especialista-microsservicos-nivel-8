package com.algaworks.algashop.ecommerce.application.model.client;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuyNowShippingCostPreviewInput {

	@NotBlank
	@Size(min = 5, max = 5)
	private String zipCode;

	@NotBlank
	private String productId;

	@NotNull
	@Positive
	private Integer quantity;

}
