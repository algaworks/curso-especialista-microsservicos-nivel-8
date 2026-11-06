package com.algaworks.algashop.product.catalog.application.product.event;

import com.algaworks.algashop.product.catalog.application.OutboundIntegrationEvent;

public interface ProductIntegrationEventPublisher {
	void send(OutboundIntegrationEvent event);
}
