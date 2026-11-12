package com.algaworks.algashop.notifications.infrastructure.mail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.UUID;

@Disabled
class EmailSenderJavaMailImplIT {

	private static final int SMTP_PORT = 1025;
	private static final String MAILPIT_HOST = "localhost";

	private MailProperties mailProperties;
	private EmailSenderJavaMailImpl emailSender;

	@BeforeEach
	void setUp() {
		mailProperties = new MailProperties();
		mailProperties.setFrom("no-reply@algashop.local");

		JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
		javaMailSender.setPort(SMTP_PORT);
		javaMailSender.setHost(MAILPIT_HOST);

		emailSender = new EmailSenderJavaMailImpl(javaMailSender, mailProperties);
	}

	@Test
	void givenValidData_whenSend_shouldNotGenerateErrors() {
		String toEmail = "john-" + UUID.randomUUID() + "@email.com";
		emailSender.send(toEmail, "John Doe", "Order confirmed", "<p>Hello John</p>");
	}

}