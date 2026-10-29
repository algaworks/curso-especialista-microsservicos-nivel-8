package com.algaworks.algashop.ecommerce.application.model.client;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class CreditCardModel {
	private String id;
	private OffsetDateTime createdAt;
	private String lastNumbers;
	private Integer expMonth;
	private Integer expYear;
	private String brand;
}
