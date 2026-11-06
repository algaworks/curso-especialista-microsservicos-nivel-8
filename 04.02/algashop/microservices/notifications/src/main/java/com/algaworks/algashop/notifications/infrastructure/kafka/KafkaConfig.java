package com.algaworks.algashop.notifications.infrastructure.kafka;

import com.algaworks.algashop.notifications.application.email.TemplateRenderException;
import com.algaworks.algashop.notifications.domain.model.template.TemplateNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.mail.MailParseException;
import org.springframework.mail.MailPreparationException;
import org.springframework.util.backoff.ExponentialBackOff;

import java.time.Duration;
import java.util.Map;

@Configuration
public class KafkaConfig {

	public static final String EMAIL_COMMANDS_TOPIC = "notifications.email.commands";
	public static final String EMAIL_COMMANDS_CONSUMER_GROUP = "notifications.email-commands";

	private static final String DLT_PREFIX = "notifications.dlt.";
	private static final int TOPIC_PARTITIONS = 3;
	private static final int TOPIC_REPLICAS = 3;
	private static final long RETENTION_30_DAYS = Duration.ofDays(30).toMillis();

	@Bean
	public NewTopic emailCommandsTopic() {
		return TopicBuilder.name(EMAIL_COMMANDS_TOPIC)
				.partitions(TOPIC_PARTITIONS)
				.replicas(TOPIC_REPLICAS)
				.configs(Map.of("min.insync.replicas", "2"))
				.build();
	}

	@Bean
	public NewTopic emailCommandsDlt() {
		return TopicBuilder.name(DLT_PREFIX + EMAIL_COMMANDS_TOPIC)
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
		DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate,
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
				IllegalArgumentException.class,
				ConstraintViolationException.class,
				TemplateNotFoundException.class,
				TemplateRenderException.class,
				MailParseException.class,
				MailPreparationException.class);
		return errorHandler;
	}
}
