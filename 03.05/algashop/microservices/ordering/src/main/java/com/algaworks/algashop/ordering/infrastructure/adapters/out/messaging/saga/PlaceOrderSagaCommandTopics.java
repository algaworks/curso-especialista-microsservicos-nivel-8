package com.algaworks.algashop.ordering.infrastructure.adapters.out.messaging.saga;

import com.algaworks.algashop.ordering.core.application.OutboundIntegrationCommand;
import com.algaworks.algashop.ordering.core.application.invoice.command.CancelInvoiceIntegrationCommand;
import com.algaworks.algashop.ordering.core.application.invoice.command.IssueInvoiceIntegrationCommand;
import com.algaworks.algashop.ordering.core.application.stock.command.ReserveStockIntegrationCommand;
import com.algaworks.algashop.ordering.infrastructure.config.kafka.AlgaShopMessagingKafkaProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PlaceOrderSagaCommandTopics {

	private final AlgaShopMessagingKafkaProperties properties;

	public String of(OutboundIntegrationCommand command) {
		return switch (command) {
			case IssueInvoiceIntegrationCommand ignored -> properties.getBillingInvoiceCommandsTopicName();
			case ReserveStockIntegrationCommand ignored -> properties.getProductCatalogStockCommandsTopicName();
			case CancelInvoiceIntegrationCommand ignored -> properties.getBillingInvoiceCommandsTopicName();
			default -> throw new IllegalArgumentException("Unsupported saga command " + command.getClass().getSimpleName());
		};
	}

}
