package com.algaworks.algashop.insights.application.insights;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TopSellingProductOutput {
	private UUID productId;
	private String productName;
	private String categoryName;
	private Long quantity;
	private BigDecimal revenue;
}
