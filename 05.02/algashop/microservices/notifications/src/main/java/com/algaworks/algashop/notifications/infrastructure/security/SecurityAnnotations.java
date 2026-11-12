package com.algaworks.algashop.notifications.infrastructure.security;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class SecurityAnnotations {

	@Target({ElementType.METHOD, ElementType.TYPE})
	@Retention(RetentionPolicy.RUNTIME)
	@PreAuthorize("hasAuthority('SCOPE_emails:send')")
	public @interface CanSendEmails {}

	@Target({ElementType.METHOD, ElementType.TYPE})
	@Retention(RetentionPolicy.RUNTIME)
	@PreAuthorize("hasAuthority('SCOPE_email-templates:read')")
	public @interface CanReadEmailTemplates {}

	@Target({ElementType.METHOD, ElementType.TYPE})
	@Retention(RetentionPolicy.RUNTIME)
	@PreAuthorize("hasAuthority('SCOPE_email-templates:write')")
	public @interface CanWriteEmailTemplates {}

}
