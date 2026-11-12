package com.algaworks.algashop.ordering.infrastructure.adapters.out.messaging.outbox;

import com.algaworks.algashop.ordering.core.application.OutboundIntegrationCommand;
import com.algaworks.algashop.ordering.core.ports.out.order.saga.ForPublishingPlaceOrderSagaIntegrationCommands;
import com.algaworks.algashop.ordering.infrastructure.adapters.out.messaging.saga.PlaceOrderSagaCommandTopics;
import com.algaworks.algashop.ordering.infrastructure.config.kafka.AlgaShopMessagingKafkaProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "algashop.messaging.outbox.enabled", havingValue = "true")
@RequiredArgsConstructor
public class OutboxPlaceOrderSagaIntegrationCommandPublisher
		implements ForPublishingPlaceOrderSagaIntegrationCommands {

	private final OutboxRecorder recorder;
	private final PlaceOrderSagaCommandTopics topics;
	private final AlgaShopMessagingKafkaProperties properties;

	@Override
	public void send(String correlationId, OutboundIntegrationCommand command) {
		recorder.record(OutboxDraft.builder()
						.aggregateId(command.getAggregateId())
						.correlationId(correlationId)
						.payload(command)
						.channelName(topics.of(command))
						.replyTopic(properties.getSagaRepliesTopicName())
				.build());
	}
}
