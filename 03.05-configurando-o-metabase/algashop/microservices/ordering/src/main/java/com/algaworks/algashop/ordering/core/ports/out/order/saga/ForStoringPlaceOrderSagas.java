package com.algaworks.algashop.ordering.core.ports.out.order.saga;

import com.algaworks.algashop.ordering.core.application.order.saga.PlaceOrderSaga;

import java.util.Optional;
import java.util.UUID;

public interface ForStoringPlaceOrderSagas {
	Optional<PlaceOrderSaga> ofId(UUID sagaId);
	void add(PlaceOrderSaga saga);
}
