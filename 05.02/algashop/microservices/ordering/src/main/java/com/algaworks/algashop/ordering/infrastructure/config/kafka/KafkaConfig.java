package com.algaworks.algashop.ordering.infrastructure.config.kafka;

import com.algaworks.algashop.ordering.core.application.saga.SagaTransitionException;
import com.algaworks.algashop.ordering.core.domain.model.DomainException;
import com.algaworks.algashop.ordering.core.domain.model.DomainEntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.ExponentialBackOff;

import java.time.Duration;
import java.util.Map;

@Configuration
public class KafkaConfig {

	private static final String DLT_PREFIX = "ordering.dlt.";
	private static final int TOPIC_PARTITIONS = 3;
	private static final int TOPIC_REPLICAS = 3;
	private static final long RETENTION_30_DAYS = Duration.ofDays(30).toMillis();

	public static final String TYPE_ID_HEADER = "__TypeId__";
	public static final String IDEMPOTENCY_KEY_HEADER = "idempotency-key";

	@Bean
	public DefaultErrorHandler defaultErrorHandler(DeadLetterPublishingRecoverer recoverer) {
		ExponentialBackOff backOff = new ExponentialBackOff(2_000L, 2);
		backOff.setMaxInterval(8_000L);
		backOff.setMaxAttempts(3);

		DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, backOff);
		errorHandler.addNotRetryableExceptions(
				DomainException.class,
				DomainEntityNotFoundException.class,
				ConstraintViolationException.class,
				DataIntegrityViolationException.class,
				IllegalArgumentException.class,
				SagaTransitionException.class);
		return errorHandler;
	}

	@Bean
	public DeadLetterPublishingRecoverer deadLetterPublishingRecoverer(
			KafkaTemplate<String, Object> kafkaTemplate) {
		DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate,
				(consumerRecord, exception) -> new TopicPartition(
						DLT_PREFIX + consumerRecord.topic(),
						consumerRecord.partition()));
		recoverer.setLogRecoveryRecord(true);
		recoverer.setFailIfSendResultIsError(false);
		return recoverer;
	}

	@Bean
	public NewTopic productEventsDlt(AlgaShopMessagingKafkaProperties properties) {
		return createDeadLetterTopic(properties.getProductEventTopicName());
	}

	@Bean
	public NewTopic orderEventsDlt(AlgaShopMessagingKafkaProperties properties) {
		return createDeadLetterTopic(properties.getOrderEventTopicName());
	}

	private NewTopic createDeadLetterTopic(String originTopicName) {
		return TopicBuilder.name(DLT_PREFIX + originTopicName)
				.partitions(TOPIC_PARTITIONS)
				.replicas(TOPIC_REPLICAS)
				.configs(Map.of(
						"min.insync.replicas", "2",
						"retention.ms", String.valueOf(RETENTION_30_DAYS)
				))
				.build();
	}

	@Bean
	public NewTopic ordersEventTopic(AlgaShopMessagingKafkaProperties properties) {
		return TopicBuilder.name(properties.getOrderEventTopicName())
				.partitions(TOPIC_PARTITIONS)
				.replicas(TOPIC_REPLICAS)
				.configs(Map.of(
						"min.insync.replicas", "2"
				))
				.build();
	}

	@Bean
	public NewTopic sagaRepliesDlt(AlgaShopMessagingKafkaProperties properties) {
		return createDeadLetterTopic(properties.getSagaRepliesTopicName());
	}

	@Bean
	public NewTopic sagaRepliesTopic(AlgaShopMessagingKafkaProperties properties) {
		return TopicBuilder.name(properties.getSagaRepliesTopicName())
				.partitions(TOPIC_PARTITIONS)
				.replicas(TOPIC_REPLICAS)
				.configs(Map.of(
						"min.insync.replicas", "2"
				))
				.build();
	}
}
