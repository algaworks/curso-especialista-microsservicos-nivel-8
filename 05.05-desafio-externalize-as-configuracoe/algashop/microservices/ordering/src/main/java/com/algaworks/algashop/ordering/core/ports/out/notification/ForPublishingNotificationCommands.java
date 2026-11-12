package com.algaworks.algashop.ordering.core.ports.out.notification;

import com.algaworks.algashop.ordering.core.application.OutboundIntegrationCommand;

public interface ForPublishingNotificationCommands {
	void send(OutboundIntegrationCommand command);
}
