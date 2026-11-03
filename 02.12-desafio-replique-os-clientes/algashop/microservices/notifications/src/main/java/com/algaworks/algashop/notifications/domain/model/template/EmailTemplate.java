package com.algaworks.algashop.notifications.domain.model.template;

import com.algaworks.algashop.notifications.domain.model.AbstractAuditableAggregateRoot;
import com.fasterxml.uuid.Generators;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "email_template")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class EmailTemplate extends AbstractAuditableAggregateRoot<EmailTemplate> {

	@Id
	@EqualsAndHashCode.Include
	private UUID id;

	@Column(nullable = false, length = 100)
	private String name;

	@Column(nullable = false)
	private String subject;

	@Column(nullable = false, columnDefinition = "text")
	private String body;

	public EmailTemplate(String name, String subject, String body) {
		this.id = Generators.timeBasedEpochRandomGenerator().generate();
		this.name = name;
		this.subject = subject;
		this.body = body;
	}

}
