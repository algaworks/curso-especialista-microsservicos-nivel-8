package com.algaworks.algashop.ordering.infrastructure.adapters.out.messaging.kafka.order;

import com.algaworks.algashop.ordering.core.application.EventPublishingException;
import com.algaworks.algashop.ordering.core.application.OutboundIntegrationEvent;
import com.algaworks.algashop.ordering.core.ports.out.order.ForPublishingOrderIntegrationEvents;
import com.algaworks.algashop.ordering.infrastructure.config.kafka.AlgaShopMessagingKafkaProperties;
import com.algaworks.algashop.ordering.infrastructure.config.kafka.KafkaConfig;
import com.algaworks.algashop.ordering.infrastructure.config.utility.BeanValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "algashop.messaging.outbox.enabled", havingValue = "false", matchIfMissing = true)
public class KafkaOrderIntegrationEventPublisher implements ForPublishingOrderIntegrationEvents {

	private final KafkaTemplate<String, Object> kafkaTemplate;
	private final AlgaShopMessagingKafkaProperties properties;
	private final BeanValidationUtil beanValidationUtil;

	@Override
	public void send(OutboundIntegrationEvent event) {
		beanValidationUtil.validate(event);
		SendResult<String, Object> result = null;
		try {
			ProducerRecord<String, Object> record = new ProducerRecord<>(
					properties.getOrderEventTopicName(),
					event.getAggregateId(),
					event);

			if (event.getIdempotencyKey() != null) {
				record.headers().add(KafkaConfig.IDEMPOTENCY_KEY_HEADER, event.getIdempotencyKey().toString().getBytes());
			}

			result = kafkaTemplate.send(record).get(40, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new EventPublishingException("Interrupted while publishing", event, e);
		} catch (TimeoutException | ExecutionException | KafkaException e) {
			throw new EventPublishingException("Failed to publish", event, e);
		}

		RecordMetadata metadata = result.getRecordMetadata();

		log.info("Publihed {} to {}-{} at offset {}",
				event.getClass().getSimpleName(),
				metadata.topic(),
				metadata.partition(),
				metadata.offset()
		);
	}
}
