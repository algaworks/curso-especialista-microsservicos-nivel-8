package com.algaworks.algashop.ordering.core.application;

import lombok.Getter;

@Getter
public class CommandPublishingException extends RuntimeException {

	private OutboundIntegrationCommand command;

	public CommandPublishingException() {
	}

	public CommandPublishingException(String message) {
		super(message);
	}

	public CommandPublishingException(String message, Throwable cause) {
		super(message, cause);
	}

	public CommandPublishingException(Throwable cause) {
		super(cause);
	}

	public CommandPublishingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public CommandPublishingException(String message,
	                                  OutboundIntegrationCommand command,
	                                  Throwable e) {
		super("%s Event=%s AggregateId=%s".formatted(message, command.getClass().getSimpleName(), command.getAggregateId()), e);
		this.command = command;
	}
}
