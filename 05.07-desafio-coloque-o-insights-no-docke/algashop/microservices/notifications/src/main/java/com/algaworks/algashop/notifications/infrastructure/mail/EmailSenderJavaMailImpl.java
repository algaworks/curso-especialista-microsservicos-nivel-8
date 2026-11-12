package com.algaworks.algashop.notifications.infrastructure.mail;

import com.algaworks.algashop.notifications.application.email.EmailSender;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailPreparationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class EmailSenderJavaMailImpl implements EmailSender {

	private final JavaMailSender mailSender;
	private final MailProperties properties;

	@Override
	public void send(String toEmail, String toName, String subject, String htmlBody) {
		MimeMessage message = mailSender.createMimeMessage();
		try {
			MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
			helper.setFrom(properties.getFrom());
			helper.setTo(new InternetAddress(toEmail, toName, StandardCharsets.UTF_8.name()));
			helper.setSubject(subject);
			helper.setText(htmlBody, true);
		} catch (Exception e) {
			throw new MailPreparationException("Fail to build email message", e);
		}
		mailSender.send(message);
	}
}
