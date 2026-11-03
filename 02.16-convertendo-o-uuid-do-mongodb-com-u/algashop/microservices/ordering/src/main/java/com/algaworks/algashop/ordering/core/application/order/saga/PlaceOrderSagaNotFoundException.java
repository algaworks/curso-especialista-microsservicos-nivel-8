package com.algaworks.algashop.ordering.core.application.order.saga;

import java.util.UUID;

public class PlaceOrderSagaNotFoundException extends RuntimeException {
	public PlaceOrderSagaNotFoundException(UUID sagaId) {
		super("Place order saga not found: " + sagaId);
	}
}
