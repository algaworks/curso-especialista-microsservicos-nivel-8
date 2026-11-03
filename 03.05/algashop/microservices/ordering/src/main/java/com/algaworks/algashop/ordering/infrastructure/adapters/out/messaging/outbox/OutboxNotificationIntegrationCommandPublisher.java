package com.algaworks.algashop.ordering.infrastructure.adapters.out.messaging.outbox;

import com.algaworks.algashop.ordering.core.application.OutboundIntegrationCommand;
import com.algaworks.algashop.ordering.core.ports.out.notification.ForPublishingNotificationCommands;
import com.algaworks.algashop.ordering.infrastructure.config.kafka.AlgaShopMessagingKafkaProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "algashop.messaging.outbox.enabled", havingValue = "true")
@RequiredArgsConstructor
public class OutboxNotificationIntegrationCommandPublisher implements ForPublishingNotificationCommands {

	private final OutboxRecorder recorder;
	private final AlgaShopMessagingKafkaProperties properties;

	@Override
	public void send(OutboundIntegrationCommand command) {
		recorder.record(OutboxDraft.builder()
				.aggregateId(command.getAggregateId())
				.payload(command)
				.channelName(properties.getNotificationsEmailCommandsTopicName())
				.build());
	}
}
