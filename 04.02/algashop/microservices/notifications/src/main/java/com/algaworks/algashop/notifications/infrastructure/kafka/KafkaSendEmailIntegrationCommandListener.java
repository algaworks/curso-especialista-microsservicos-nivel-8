package com.algaworks.algashop.notifications.infrastructure.kafka;

import com.algaworks.algashop.notifications.application.email.SendEmailApplicationService;
import com.algaworks.algashop.notifications.application.email.command.SendEmailIntegrationCommand;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
@KafkaListener(
		id = KafkaConfig.EMAIL_COMMANDS_CONSUMER_GROUP,
		concurrency = "3",
		topics = KafkaConfig.EMAIL_COMMANDS_TOPIC)
public class KafkaSendEmailIntegrationCommandListener {

	private final SendEmailApplicationService sendEmailApplicationService;

	@KafkaHandler
	public void handle(
			@Payload @Valid SendEmailIntegrationCommand command,
			@Header(value = KafkaHeaders.RECEIVED_KEY, required = false) String messageKey,
			@Header(value = KafkaHeaders.RECEIVED_PARTITION, required = false) Integer partition,
			@Header(value = KafkaHeaders.OFFSET, required = false) Long offset) {
		log.info("Received SendEmailCommand | templateId={} | key={} | partition={} | offset={}",
				command.getTemplateId(), messageKey, partition, offset);

		sendEmailApplicationService.send(command.toInput());
	}

	// o tópico é exclusivo de comandos de e-mail: tipo desconhecido é erro de contrato
	@KafkaHandler(isDefault = true)
	public void handle(
			@Payload Object command,
			@Header(value = KafkaHeaders.RECEIVED_KEY, required = false) String messageKey,
			@Header(value = KafkaHeaders.OFFSET, required = false) Long offset) {
		log.warn("Unsupported email command: type={} key={} offset={}",
				command.getClass().getSimpleName(), messageKey, offset);
		throw new IllegalArgumentException(
				"Unsupported email command type " + command.getClass().getSimpleName());
	}
}
