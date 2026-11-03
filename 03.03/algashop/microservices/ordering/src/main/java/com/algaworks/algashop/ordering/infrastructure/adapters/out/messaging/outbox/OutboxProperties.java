package com.algaworks.algashop.ordering.infrastructure.adapters.out.messaging.outbox;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@Component
@Data
@ConfigurationProperties("algashop.messaging.outbox")
public class OutboxProperties {

	@NotNull
	private boolean enabled = false;

	@NotNull
	private Dispatcher dispatcher = new Dispatcher();

	@NotNull
	private Duration sendTimeout;

	@Min(1)
	private int batchSize;

	@NotNull
	private Duration pollInterval;

	@Min(1)
	private int maxAttempts;

	@NotNull
	private Duration backoff;

	@NotNull
	private Duration batchDeadLine;

	@Data
	public static class Dispatcher {
		private boolean enabled = false;
	}
}
