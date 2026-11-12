package com.algaworks.algashop.ordering.core.ports.out.order.saga;

import com.algaworks.algashop.ordering.core.application.OutboundIntegrationCommand;

public interface ForPublishingPlaceOrderSagaIntegrationCommands {
	void send(String correlationId, OutboundIntegrationCommand command);
}
