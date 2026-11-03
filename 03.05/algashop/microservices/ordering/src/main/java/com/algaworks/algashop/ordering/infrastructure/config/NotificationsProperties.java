package com.algaworks.algashop.ordering.infrastructure.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.UUID;

@Component
@Validated
@Data
@ConfigurationProperties("algashop.notifications")
public class NotificationsProperties {

	@NotNull
	@Valid
	private EmailTemplates emailTemplates;

	@Data
	public static class EmailTemplates {

		/** Id do template "pedido recebido" cadastrado no Notifications. */
		@NotNull
		private UUID orderPlaced;
	}
}
