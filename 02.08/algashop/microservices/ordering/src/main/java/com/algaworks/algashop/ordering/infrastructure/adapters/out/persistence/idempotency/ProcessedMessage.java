package com.algaworks.algashop.ordering.infrastructure.adapters.out.persistence.idempotency;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;

import java.util.UUID;

@Entity
@Table(name = "processed_message")
@NoArgsConstructor
public class ProcessedMessage implements Persistable<UUID> {

	@Id
	private UUID idempotencyKey;

	public ProcessedMessage(UUID idempotencyKey) {
		this.idempotencyKey = idempotencyKey;
	}

	@Override
	public UUID getId() {
		return idempotencyKey;
	}

	@Override
	public boolean isNew() {
		return true; // essa tabela só insere, nunca atualiza
	}
}
