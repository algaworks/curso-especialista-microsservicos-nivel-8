package com.algaworks.algashop.ecommerce.application.model.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceModel {

	private String orderCode;

	private String customerId;

	private OffsetDateTime createdAt;

	private OffsetDateTime paidAt;

	private OffsetDateTime canceledAt;

	private String status;

	private PaymentModel currentPayment;
}
