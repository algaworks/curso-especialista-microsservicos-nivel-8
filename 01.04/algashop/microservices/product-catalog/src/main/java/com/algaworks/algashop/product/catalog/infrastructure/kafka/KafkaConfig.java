package com.algaworks.algashop.product.catalog.infrastructure.kafka;

import com.algaworks.algashop.product.catalog.application.EventPublishingException;
import com.algaworks.algashop.product.catalog.application.OutboundIntegrationEvent;
import com.algaworks.algashop.product.catalog.application.product.event.ProductIntegrationEventPublisher;
import com.algaworks.algashop.product.catalog.domain.model.DomainException;
import com.algaworks.algashop.product.catalog.infrastructure.persistence.product.StockUpdateFailed;
import com.algaworks.algashop.product.catalog.infrastructure.utility.BeanValidationUtil;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.backoff.ExponentialBackOff;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class KafkaConfig {

	private static final String DLT_PREFIX = "product-catalog.dlt.";

	public static final String TYPE_ID_HEADER = "__TypeId__";
	public static final String IDEMPOTENCY_KEY_HEADER = "idempotency-key";

	private static final int TOPIC_PARTITIONS = 3;
	private static final int TOPIC_REPLICAS = 3;
	private static final long RETENTION_30_DAYS = Duration.ofDays(30).toMillis();

	private final AlgaShopMessagingKafkaProperties properties;

	@Bean
	public NewTopic productsEventTopic() {
		return TopicBuilder.name(properties.getProductEventTopicName())
				.partitions(TOPIC_PARTITIONS)
				.replicas(TOPIC_REPLICAS)
				.configs(Map.of("min.insync.replicas", "2"))
				.build();
	}

	@Bean
	public NewTopic stockCommandsTopic() {
		return TopicBuilder.name(properties.getStockCommandsTopicName())
				.partitions(TOPIC_PARTITIONS)
				.replicas(TOPIC_REPLICAS)
				.configs(Map.of("min.insync.replicas", "2"))
				.build();
	}

	private NewTopic deadLetterTopic(String sourceTopic) {
		return TopicBuilder.name(DLT_PREFIX + sourceTopic)
				.partitions(TOPIC_PARTITIONS)
				.replicas(TOPIC_REPLICAS)
				.configs(Map.of(
						"min.insync.replicas", "2",
						"retention.ms", String.valueOf(RETENTION_30_DAYS)))
				.build();
	}

	@Bean
	public DeadLetterPublishingRecoverer deadLetterPublishingRecoverer(
			KafkaTemplate<String, Object> kafkaTemplate) {
		DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
				kafkaTemplate,
				(record, exception) -> new TopicPartition(DLT_PREFIX + record.topic(), record.partition()));
		recoverer.setFailIfSendResultIsError(false);
		recoverer.setLogRecoveryRecord(true);
		return recoverer;
	}

	@Bean
	public DefaultErrorHandler defaultErrorHandler(DeadLetterPublishingRecoverer recoverer) {
		ExponentialBackOff backOff = new ExponentialBackOff(2_000L, 2);
		backOff.setMaxInterval(8_000L);
		backOff.setMaxAttempts(3);

		DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, backOff);
		errorHandler.addNotRetryableExceptions(
				DomainException.class,
				ConstraintViolationException.class,
				DataIntegrityViolationException.class,
				DuplicateKeyException.class,
				IllegalArgumentException.class);
		errorHandler.addRetryableExceptions(StockUpdateFailed.class);
		return errorHandler;
	}

	@Bean
	public ProductIntegrationEventPublisher productIntegrationEventPublisher(
			KafkaTemplate<String, Object> kafkaTemplate,
			AlgaShopMessagingKafkaProperties properties,
			BeanValidationUtil beanValidationUtil) {
		return event -> publish(event, properties.getProductEventTopicName(),
				kafkaTemplate, beanValidationUtil);
	}

	private void publish(
			OutboundIntegrationEvent event,
			String destination,
			KafkaTemplate<String, Object> kafkaTemplate,
			BeanValidationUtil beanValidationUtil) {
		beanValidationUtil.validate(event);
		SendResult<String, Object> result;
		try {
			ProducerRecord<String, Object> record = new ProducerRecord<>(
					destination,
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

		log.info("Published {} to {}-{} at offset {}",
				event.getClass().getSimpleName(),
				metadata.topic(),
				metadata.partition(),
				metadata.offset()
			);
	}

}
