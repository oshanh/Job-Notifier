package org.oshanh.jobnotifier.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.oshanh.jobnotifier.dto.Job;
import org.oshanh.jobnotifier.model.Topjobs;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class NotificationService {
	private final JavaMailSender mailSender;

	public void sendGmailNotification(String toEmail, String subject, String messageBody) {
		validateInput(toEmail, subject, messageBody);

		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(toEmail.trim());
		message.setSubject(subject.trim());
		message.setText(messageBody.trim());

		try {
			mailSender.send(message);
		} catch (MailException ex) {
			throw new MailSendException("Failed to send Gmail notification", ex);
		}
	}

	public void sendNewJobPostingsNotification(String toEmail, List<Job> newJobs) {
		validateJobNotificationInput(toEmail, newJobs);

		String subject =  newJobs.get(0).getCompanyName()+"-"+newJobs.size()+" New Jobs ";
		String htmlBody = buildJobPostingsHtml(newJobs);

		try {
			MimeMessage mimeMessage = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
			helper.setTo(toEmail.trim());
			helper.setSubject(subject);
			helper.setText(htmlBody, true);
			mailSender.send(mimeMessage);
		} catch (MessagingException | MailException ex) {
			throw new MailSendException("Failed to send job postings notification", ex);
		}
	}

	private void validateInput(String toEmail, String subject, String messageBody) {
		if (isBlank(toEmail)) {
			throw new IllegalArgumentException("Recipient email is required");
		}
		if (isBlank(subject)) {
			throw new IllegalArgumentException("Email subject is required");
		}
		if (isBlank(messageBody)) {
			throw new IllegalArgumentException("Email message body is required");
		}
	}

	private boolean isBlank(String value) {
		return Objects.isNull(value) || value.trim().isEmpty();
	}

	private void validateJobNotificationInput(String toEmail, List<Job> newJobs) {
		if (isBlank(toEmail)) {
			throw new IllegalArgumentException("Recipient email is required");
		}
		if (Objects.isNull(newJobs) || newJobs.isEmpty()) {
			throw new IllegalArgumentException("Job list must contain at least one item");
		}
	}

	private String buildJobPostingsHtml(List<Job> jobs) {
		StringBuilder html = new StringBuilder();
		html.append("<!DOCTYPE html><html><head><meta charset='UTF-8'></head>");
		html.append("<body style='margin:0;padding:0;background:#f3f6fb;font-family:Segoe UI,Arial,sans-serif;color:#1f2937;'>");
		html.append("<div style='max-width:680px;margin:24px auto;padding:20px;'>");
		html.append("<div style='background:linear-gradient(120deg,#0f4c81,#2a9d8f);color:#ffffff;border-radius:16px;padding:20px 22px;'>");
		html.append("<h2 style='margin:0;font-size:24px;'>New Job Opportunities</h2>");
		html.append("<p style='margin:8px 0 0 0;font-size:14px;opacity:0.95;'>Fresh listings matched from your sources.</p>");
		html.append("</div>");

		for (Job job : jobs) {
			html.append("<div style='background:#ffffff;border:1px solid #dbe3ef;border-radius:14px;padding:16px;margin-top:14px;'>");
			html.append("<p style='margin:0 0 8px 0;color:#6b7280;font-size:12px;letter-spacing:0.4px;'>POSITION</p>");
			html.append("<h3 style='margin:0 0 14px 0;font-size:20px;line-height:1.3;color:#111827;'>")
					.append(escapeHtml(job.getPosition()))
					.append("</h3>");
			html.append("<p style='margin:0 0 8px 0;color:#6b7280;font-size:12px;letter-spacing:0.4px;'>COMPANY</p>");
			html.append("<p style='margin:0 0 14px 0;font-size:16px;color:#1f2937;'>")
					.append(escapeHtml(job.getCompanyName()))
					.append("</p>");
			html.append("<p style='margin:0 0 8px 0;color:#6b7280;font-size:12px;letter-spacing:0.4px;'>SOURCE</p>");
			html.append("<a href='")
					.append(escapeHtml(job.getSource()))
					.append("' style='display:inline-block;padding:10px 14px;background:#0f4c81;color:#ffffff;text-decoration:none;border-radius:10px;font-size:14px;'>View Job</a>");
			html.append("</div>");
		}

		html.append("<p style='margin:18px 4px 0 4px;color:#6b7280;font-size:12px;'>")
				.append("You are receiving this because job alerts are enabled for your account.")
				.append("</p>");
		html.append("</div></body></html>");
		return html.toString();
	}

	private String escapeHtml(String value) {
		if (isBlank(value)) {
			return "N/A";
		}
		return value
				.replace("&", "&amp;")
				.replace("<", "&lt;")
				.replace(">", "&gt;")
				.replace("\"", "&quot;")
				.replace("'", "&#39;");
	}
}
