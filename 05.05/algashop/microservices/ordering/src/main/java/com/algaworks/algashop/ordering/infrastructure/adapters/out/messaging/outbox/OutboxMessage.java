package com.algaworks.algashop.ordering.infrastructure.adapters.out.messaging.outbox;

import com.algaworks.algashop.ordering.core.domain.model.IdGenerator;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.type.PostgreSQLJsonPGObjectJsonType;
import org.springframework.data.domain.Persistable;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "outbox")
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class OutboxMessage implements Persistable<UUID> {

	@Id
	private UUID id;

	private String channelName;

	private String replyTopic;

	private String aggregateId;

	private String correlationId;

	private String messageType;

	@JdbcType(PostgreSQLJsonPGObjectJsonType.class)
	private String payload;

	private OffsetDateTime createdAt;

	private int attempts;

	private OffsetDateTime nextAttemptAt;

	private String lastError;

	private OffsetDateTime failedAt;

	@Builder
	public OutboxMessage(String channelName, String aggregateId,
	                     String messageType, String payload, String correlationId, String replyTopic) {
		this.id = IdGenerator.generateTimeBasedUUID();
		this.createdAt = OffsetDateTime.now();
		this.nextAttemptAt = OffsetDateTime.now();

		this.channelName = channelName;
		this.aggregateId = aggregateId;
		this.messageType = messageType;
		this.payload = payload;

		this.correlationId = correlationId;
		this.replyTopic = replyTopic;
	}

	@Override
	public boolean isNew() {
		return true;
	}
}
