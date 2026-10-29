package com.algaworks.algashop.ordering.core.application.order.saga;

import java.util.Arrays;
import java.util.List;

public enum PlaceOrderSagaStep {
	PLACING_ORDER,
	INVOICING(PLACING_ORDER),
	RESERVING_STOCK(INVOICING),
	APPROVING_ORDER(RESERVING_STOCK),
	
	CANCELLING_INVOICE(RESERVING_STOCK),
	CANCELLING_ORDER(INVOICING, CANCELLING_INVOICE);

	PlaceOrderSagaStep(PlaceOrderSagaStep... previousStatuses) {
		this.previousStatuses = Arrays.asList(previousStatuses);
	}

	private final List<PlaceOrderSagaStep> previousStatuses;

	public boolean canChangeTo(PlaceOrderSagaStep newStatus) {
		PlaceOrderSagaStep currentStatus = this;
		return newStatus.previousStatuses.contains(currentStatus);
	}

	public boolean canNotChangeTo(PlaceOrderSagaStep newStatus) {
		return !canChangeTo(newStatus);
	}
}
