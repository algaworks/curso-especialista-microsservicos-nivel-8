package com.algaworks.algashop.ordering.infrastructure.adapters.out.messaging.outbox;

import com.algaworks.algashop.ordering.infrastructure.config.kafka.KafkaConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Qualifier;
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
@ConditionalOnProperty(name = "algashop.messaging.outbox.dispatcher.enabled", havingValue = "true")
@Slf4j
public class OutboxKafkaSender {

	private final KafkaTemplate<String, byte[]> kafkaTemplate;
	private final OutboxProperties outboxProperties;

	public OutboxKafkaSender(@Qualifier("outboxKafkaTemplate") KafkaTemplate<String, byte[]> kafkaTemplate,
	                         OutboxProperties outboxProperties) {
		this.kafkaTemplate = kafkaTemplate;
		this.outboxProperties = outboxProperties;
	}

	public void send(OutboxMessage message) {
		ProducerRecord<String, byte[]> record = new ProducerRecord<>(
				message.getChannelName(),
				message.getAggregateId(),
				message.getPayload().getBytes(StandardCharsets.UTF_8)
		);

		record.headers().add(KafkaConfig.TYPE_ID_HEADER, message.getMessageType().getBytes(StandardCharsets.UTF_8));
		record.headers().add(KafkaConfig.IDEMPOTENCY_KEY_HEADER, message.getId().toString().getBytes(StandardCharsets.UTF_8));

		if (message.getCorrelationId() != null) {
			record.headers().add(KafkaHeaders.CORRELATION_ID, message.getCorrelationId().getBytes(StandardCharsets.UTF_8));
		}

		if (message.getReplyTopic() != null) {
			record.headers().add(KafkaHeaders.REPLY_TOPIC, message.getReplyTopic().getBytes(StandardCharsets.UTF_8));
		}

		SendResult<String, byte[]> result = doSend(record);

		RecordMetadata metadata = result.getRecordMetadata();
		log.info("Published {} from outbox to {}-{} at offset {} | messageId={} aggregateId={}",
				message.getMessageType(), metadata.topic(), metadata.partition(),
				metadata.offset(), message.getId(), message.getAggregateId());

	}

	private SendResult<String, byte[]> doSend(ProducerRecord<String, byte[]> record) {
		try {
			return kafkaTemplate.send(record).get(outboxProperties.getSendTimeout().toMillis(), TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new OutboxSendException("Interrupted while publishing", e);
		} catch (ExecutionException | TimeoutException | KafkaException e) {
			throw new OutboxSendException("Fail to publish", e);
		}
	}
}
