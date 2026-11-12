package com.algaworks.algashop.ordering.infrastructure.adapters.out.messaging.outbox;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;

import java.util.Map;

@Configuration
public class OutboxConfig {

	@Bean
	@ConditionalOnProperty(name = "algashop.messaging.outbox.enabled", havingValue = "true")
	public JacksonJsonSerializer<Object> outboxJsonSerializer(
			@Value("${spring.kafka.properties.spring.json.type.mapping:}") String typeMappings
	) {
		JacksonJsonSerializer<Object> serializer = new JacksonJsonSerializer<>();
		serializer.configure(Map.of(JacksonJsonSerializer.TYPE_MAPPINGS, typeMappings), false);
		return serializer;
	}

	@Bean(defaultCandidate = false)
	@ConditionalOnProperty(name = "algashop.messaging.outbox.dispatcher.enabled", havingValue = "true")
	public KafkaTemplate<String, byte[]> outboxKafkaTemplate(
			ProducerFactory<String, byte[]> producerFactory
	) {
		return new KafkaTemplate<>(producerFactory,
				Map.of(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class));
	}

}
