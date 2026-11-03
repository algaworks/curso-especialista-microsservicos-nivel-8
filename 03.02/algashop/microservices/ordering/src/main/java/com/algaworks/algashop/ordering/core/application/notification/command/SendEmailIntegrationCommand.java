package com.algaworks.algashop.ordering.core.application.notification.command;

import com.algaworks.algashop.ordering.core.application.OutboundIntegrationCommand;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SendEmailIntegrationCommand implements OutboundIntegrationCommand {

	@NotBlank
	private String aggregateId;

	@NotNull
	@Valid
	private Recipient recipient;

	@NotNull
	private UUID templateId;

	@Builder.Default
	private Map<String, Object> variables = new HashMap<>();

	@Data
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Recipient {

		@NotBlank
		private String email;

		private String name;
	}
}
