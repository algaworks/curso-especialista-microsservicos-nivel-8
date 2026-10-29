package com.algaworks.algashop.notifications.application.email;

import com.algaworks.algashop.notifications.domain.model.template.EmailTemplate;
import com.algaworks.algashop.notifications.domain.model.template.EmailTemplateRepository;
import com.algaworks.algashop.notifications.domain.model.template.TemplateNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SendEmailApplicationServiceTest {

	@Mock
	private EmailTemplateRepository templateRepository;

	@Mock
	private EmailTemplateRenderer renderer;

	@Mock
	private EmailSender emailSender;

	@InjectMocks
	private SendEmailApplicationService service;

	private final EmailTemplate template = new EmailTemplate("welcome", "Hi", "<p>Hello</p>");

	private SendEmailInput input(Map<String, Object> variables) {
		return new SendEmailInput("john.doe@example.com", "John Doe", template.getId(), variables);
	}

	@Test
	void givenExistingTemplate_whenSend_shouldRenderThenSend() {
		when(templateRepository.findById(template.getId())).thenReturn(Optional.of(template));
		when(renderer.renderSubject(eq("Hi"), any())).thenReturn("Hi John Doe");
		when(renderer.renderBody(eq("<p>Hello</p>"), any())).thenReturn("<p>Hello John Doe</p>");

		service.send(input(Map.of("orderId", "ORD-1001")));

		var order = inOrder(renderer, emailSender);
		order.verify(renderer).renderSubject(eq("Hi"), any());
		order.verify(renderer).renderBody(eq("<p>Hello</p>"), any());
		order.verify(emailSender).send("john.doe@example.com", "John Doe", "Hi John Doe", "<p>Hello John Doe</p>");
	}

	@Test
	void givenMissingTemplate_whenSend_shouldThrowAndNotRenderNorSend() {
		UUID templateId = UUID.randomUUID();
		when(templateRepository.findById(templateId)).thenReturn(Optional.empty());

		assertThatExceptionOfType(TemplateNotFoundException.class)
				.isThrownBy(() -> service.send(new SendEmailInput("a@b.com", "A", templateId, Map.of())));

		verifyNoInteractions(renderer, emailSender);
	}

	@Test
	void givenVariables_whenSend_shouldExposeRecipientAndVariables() {
		when(templateRepository.findById(template.getId())).thenReturn(Optional.of(template));

		service.send(input(Map.of("orderId", "ORD-1001")));

		ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.captor();
		verify(renderer).renderSubject(anyString(), captor.capture());
		assertThat(captor.getValue())
				.containsEntry("orderId", "ORD-1001")
				.containsEntry("recipient", Map.of("email", "john.doe@example.com", "name", "John Doe"));
	}

	@Test
	void givenVariablesWithRecipient_whenSend_shouldKeepCommandRecipient() {
		when(templateRepository.findById(template.getId())).thenReturn(Optional.of(template));

		service.send(input(Map.of("recipient", Map.of("email", "evil@example.com", "name", "Evil"))));

		ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.captor();
		verify(renderer).renderBody(anyString(), captor.capture());
		assertThat(captor.getValue())
				.containsEntry("recipient", Map.of("email", "john.doe@example.com", "name", "John Doe"));
	}

	@Test
	void givenNullVariables_whenSend_shouldStillSend() {
		when(templateRepository.findById(template.getId())).thenReturn(Optional.of(template));

		service.send(input(null));

		verify(emailSender).send(eq("john.doe@example.com"), eq("John Doe"), any(), any());
	}

	@Test
	void givenRenderFailure_whenSend_shouldPropagateAndNotSend() {
		when(templateRepository.findById(template.getId())).thenReturn(Optional.of(template));
		when(renderer.renderSubject(anyString(), any())).thenThrow(new TemplateRenderException("boom", null));

		assertThatExceptionOfType(TemplateRenderException.class).isThrownBy(() -> service.send(input(Map.of())));

		verifyNoInteractions(emailSender);
	}
}
