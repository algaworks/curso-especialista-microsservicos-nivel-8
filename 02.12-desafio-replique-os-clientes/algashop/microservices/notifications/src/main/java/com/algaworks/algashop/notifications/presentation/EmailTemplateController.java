package com.algaworks.algashop.notifications.presentation;

import com.algaworks.algashop.notifications.application.template.management.EmailTemplateInput;
import com.algaworks.algashop.notifications.application.template.management.EmailTemplateManagementApplicationService;
import com.algaworks.algashop.notifications.application.template.query.EmailTemplateOutput;
import com.algaworks.algashop.notifications.application.template.query.EmailTemplateQueryService;
import com.algaworks.algashop.notifications.infrastructure.security.SecurityAnnotations.CanReadEmailTemplates;
import com.algaworks.algashop.notifications.infrastructure.security.SecurityAnnotations.CanWriteEmailTemplates;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/email-templates")
@RequiredArgsConstructor
public class EmailTemplateController {

	private final EmailTemplateManagementApplicationService managementService;
	private final EmailTemplateQueryService queryService;

	@PostMapping
	@CanWriteEmailTemplates
	public ResponseEntity<EmailTemplateOutput> create(@RequestBody @Valid EmailTemplateInput input) {
		UUID id = managementService.create(input);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}").buildAndExpand(id).toUri();
		return ResponseEntity.created(location).body(queryService.findById(id));
	}

	@GetMapping
	@CanReadEmailTemplates
	public PagedModel<EmailTemplateOutput> list(Pageable pageable) {
		return new PagedModel<>(queryService.findAll(pageable));
	}

	@GetMapping("/{id}")
	@CanReadEmailTemplates
	public EmailTemplateOutput get(@PathVariable UUID id) {
		return queryService.findById(id);
	}

	@PutMapping("/{id}")
	@CanWriteEmailTemplates
	public EmailTemplateOutput update(@PathVariable UUID id, @RequestBody @Valid EmailTemplateInput input) {
		managementService.update(id, input);
		return queryService.findById(id);
	}

	@DeleteMapping("/{id}")
	@CanWriteEmailTemplates
	public ResponseEntity<Void> delete(@PathVariable UUID id) {
		managementService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
