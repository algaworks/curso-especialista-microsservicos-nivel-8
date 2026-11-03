package com.algaworks.algashop.ordering.infrastructure.adapters.out.messaging.outbox;

import com.algaworks.algashop.ordering.core.application.OutboundIntegrationEvent;
import com.algaworks.algashop.ordering.core.ports.out.order.ForPublishingOrderIntegrationEvents;
import com.algaworks.algashop.ordering.infrastructure.config.kafka.AlgaShopMessagingKafkaProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "algashop.messaging.outbox.enabled", havingValue = "true")
@RequiredArgsConstructor
public class OutboxOrderIntegrationEventPublisher implements ForPublishingOrderIntegrationEvents {

	private final OutboxRecorder recorder;
	private final AlgaShopMessagingKafkaProperties properties;

	@Override
	public void send(OutboundIntegrationEvent event) {
		recorder.record(OutboxDraft.builder()
				.channelName(properties.getOrderEventTopicName())
				.aggregateId(event.getAggregateId())
				.payload(event)
				.build()
		);
	}
}
