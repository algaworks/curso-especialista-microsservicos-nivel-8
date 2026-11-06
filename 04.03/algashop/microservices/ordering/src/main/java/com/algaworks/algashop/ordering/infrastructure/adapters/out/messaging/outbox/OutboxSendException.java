package com.algaworks.algashop.ordering.infrastructure.adapters.out.messaging.outbox;

public class OutboxSendException extends RuntimeException {
	public OutboxSendException(String message) {
		super(message);
	}

	public OutboxSendException(String message, Throwable cause) {
		super(message, cause);
	}
}
