package com.algaworks.algashop.ordering.core.ports.out.order;

import com.algaworks.algashop.ordering.core.application.OutboundIntegrationEvent;

public interface ForPublishingOrderIntegrationEvents {
	void send(OutboundIntegrationEvent event);
}
