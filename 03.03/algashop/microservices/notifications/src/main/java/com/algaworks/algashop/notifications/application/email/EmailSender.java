package com.algaworks.algashop.notifications.application.email;

public interface EmailSender {
	void send(String toEmail, String toName, String subject, String htmlBody);
}
