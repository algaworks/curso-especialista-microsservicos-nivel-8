package com.algaworks.algashop.ordering.infrastructure.adapters.out.messaging.kafka.saga;

import com.algaworks.algashop.ordering.core.application.CommandPublishingException;
import com.algaworks.algashop.ordering.core.application.OutboundIntegrationCommand;
import com.algaworks.algashop.ordering.core.ports.out.order.saga.ForPublishingPlaceOrderSagaIntegrationCommands;
import com.algaworks.algashop.ordering.infrastructure.adapters.out.messaging.saga.PlaceOrderSagaCommandTopics;
import com.algaworks.algashop.ordering.infrastructure.config.kafka.AlgaShopMessagingKafkaProperties;
import com.algaworks.algashop.ordering.infrastructure.config.kafka.KafkaConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "algashop.messaging.outbox.enabled", havingValue = "false", matchIfMissing = true)
public class KafkaPlaceOrderSagaIntegrationCommandPublisher implements ForPublishingPlaceOrderSagaIntegrationCommands {

	private final KafkaTemplate<String, Object> kafkaTemplate;
	private final PlaceOrderSagaCommandTopics topics;
	private final AlgaShopMessagingKafkaProperties properties;

	@Override
	public void send(String correlationId, OutboundIntegrationCommand command) {
		ProducerRecord<String, Object> record = new ProducerRecord<>(
				topics.of(command),
				command.getAggregateId(),
				command);

		record.headers().add(KafkaHeaders.CORRELATION_ID, correlationId.getBytes(StandardCharsets.UTF_8));
		record.headers().add(KafkaHeaders.REPLY_TOPIC,
				properties.getSagaRepliesTopicName().getBytes(StandardCharsets.UTF_8));

		if (command.getIdempotencyKey() != null) {
			record.headers().add(KafkaConfig.IDEMPOTENCY_KEY_HEADER,
					command.getIdempotencyKey().toString().getBytes(StandardCharsets.UTF_8));
		}

		SendResult<String, Object> result;
		try {
			result = kafkaTemplate.send(record).get(40, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new CommandPublishingException("Interrupted while publishing", command, e);
		} catch (TimeoutException | ExecutionException | KafkaException e) {
			throw new CommandPublishingException("Failed to publish", command, e);
		}

		RecordMetadata metadata = result.getRecordMetadata();
		log.info("Published {} to {}-{} at offset {}",
				command.getClass().getSimpleName(),
				metadata.topic(),
				metadata.partition(),
				metadata.offset());
	}
}
