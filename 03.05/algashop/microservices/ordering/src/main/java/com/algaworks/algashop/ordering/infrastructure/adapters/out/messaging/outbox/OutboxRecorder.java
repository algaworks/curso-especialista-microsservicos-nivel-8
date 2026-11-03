package com.algaworks.algashop.ordering.infrastructure.adapters.out.messaging.outbox;

import com.algaworks.algashop.ordering.infrastructure.config.kafka.KafkaConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

@Component
@ConditionalOnProperty(name = "algashop.messaging.outbox.enabled", havingValue = "true")
@RequiredArgsConstructor
@Slf4j
public class OutboxRecorder {

	private final OutboxMessageRepository repository;
	private final JacksonJsonSerializer<Object> outboxJsonSerializer;

	@Transactional(propagation = Propagation.MANDATORY)
	public void record(OutboxDraft draft) {
		Object message = draft.payload();
		RecordHeaders headers = new RecordHeaders();
		byte[] payload = outboxJsonSerializer.serialize(draft.channelName(), headers, message);

		String messageType = readMessageType(headers, message);

		OutboxMessage outboxMessage = OutboxMessage.builder()
				.channelName(draft.channelName())
				.aggregateId(draft.aggregateId())
				.messageType(messageType)
				.replyTopic(draft.replyTopic())
				.correlationId(draft.correlationId())
				.payload(new String(payload, StandardCharsets.UTF_8))
				.build();

		repository.save(outboxMessage);

		log.info("Recorder {} on outbox: channel={} aggregateId{} id={}", message.getClass().getSimpleName(),
				draft.channelName(), draft.aggregateId(), outboxMessage.getId());

	}

	private String readMessageType(RecordHeaders headers, Object message) {
		Header typeId = headers.lastHeader(KafkaConfig.TYPE_ID_HEADER);
		if (typeId == null) {
			return message.getClass().getName();
		}
		return new String(typeId.value(), StandardCharsets.UTF_8);
	}

}
