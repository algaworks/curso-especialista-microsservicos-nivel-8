package com.algaworks.algashop.notifications.application.template.management;

import com.algaworks.algashop.notifications.application.email.EmailTemplateHtmlValidator;
import com.algaworks.algashop.notifications.application.email.EmailTemplateRenderer;
import com.algaworks.algashop.notifications.domain.model.template.EmailTemplate;
import com.algaworks.algashop.notifications.domain.model.template.EmailTemplateRepository;
import com.algaworks.algashop.notifications.domain.model.template.TemplateNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailTemplateManagementApplicationService {

	private final EmailTemplateRepository repository;
	private final EmailTemplateRenderer renderer;
	private final EmailTemplateHtmlValidator htmlValidator;

	@Transactional
	public UUID create(EmailTemplateInput input) {
		validateContent(input);
		return repository.saveAndFlush(new EmailTemplate(input.name(), input.subject(), input.body())).getId();
	}

	@Transactional
	public void update(UUID id, EmailTemplateInput input) {
		EmailTemplate template = repository.findById(id).orElseThrow(() -> new TemplateNotFoundException(id));
		validateContent(input);
		template.setName(input.name());
		template.setSubject(input.subject());
		template.setBody(input.body());
		repository.saveAndFlush(template);
	}

	@Transactional
	public void delete(UUID id) {
		EmailTemplate template = repository.findById(id).orElseThrow(() -> new TemplateNotFoundException(id));
		repository.delete(template);
	}

	private void validateContent(EmailTemplateInput input) {
		renderer.assertRenderable(input.subject(), input.body());
		htmlValidator.validateSyntax(input.body());
	}
}
