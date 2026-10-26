package com.algaworks.algashop.notifications.domain.model.template;

import com.algaworks.algashop.notifications.domain.model.DomainEntityNotFoundException;

import java.util.UUID;

public class TemplateNotFoundException extends DomainEntityNotFoundException {

	public TemplateNotFoundException(UUID templateId) {
		super("Email template not found: " + templateId);
	}
}
