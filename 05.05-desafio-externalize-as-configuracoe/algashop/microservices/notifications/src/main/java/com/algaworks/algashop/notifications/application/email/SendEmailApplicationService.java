package com.algaworks.algashop.notifications.application.email;

import com.algaworks.algashop.notifications.domain.model.template.EmailTemplate;
import com.algaworks.algashop.notifications.domain.model.template.EmailTemplateRepository;
import com.algaworks.algashop.notifications.domain.model.template.TemplateNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SendEmailApplicationService {

	private final EmailTemplateRepository templateRepository;
	private final EmailTemplateRenderer renderer;
	private final EmailSender emailSender;

	public void send(SendEmailInput input) {
		EmailTemplate template = templateRepository.findById(input.templateId())
				.orElseThrow(() -> new TemplateNotFoundException(input.templateId()));
		Map<String, Object> variables = buildVariables(input);
		String subject = renderer.renderSubject(template.getSubject(), variables);
		String body = renderer.renderBody(template.getBody(), variables);

		emailSender.send(input.recipientEmail(), input.recipientName(), subject, body);

		log.info("Email sent: templateId={}", template.getId());
	}

	private Map<String, Object> buildVariables(SendEmailInput input) {
		Map<String, Object> variables = new HashMap<>();
		if (input.variables() != null) {
			variables.putAll(input.variables());
		}
		Map<String, Object> recipient = new HashMap<>();
		recipient.put("email", input.recipientEmail());
		recipient.put("name", input.recipientName());
		variables.put("recipient", recipient);
		return variables;
	}
}
