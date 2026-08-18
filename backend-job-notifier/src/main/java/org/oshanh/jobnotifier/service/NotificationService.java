package org.oshanh.jobnotifier.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.oshanh.jobnotifier.dto.JobDTO;
import org.oshanh.jobnotifier.model.FosmisNotice;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
	private final JavaMailSender mailSender;

	public boolean sendTestGmailNotification(String toEmail, String subject, String messageBody) {
		validateInput(toEmail, subject, messageBody);

		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(toEmail.trim());
		message.setSubject(subject.trim());
		message.setText(messageBody.trim());

		try {
			mailSender.send(message);
			return true;
		} catch (MailException ex) {
			throw new MailSendException("Failed to send Gmail notification", ex);

		}
	}

	public boolean sendAiResponseGmail(String toEmail, String subject, String messageBody) {
		try {
			// 1. Create a MimeMessage instead of SimpleMailMessage
			MimeMessage mimeMessage = mailSender.createMimeMessage();

			// 2. Use the helper to set up the email details (true = multipart/HTML enabled)
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

			helper.setTo(toEmail);
			helper.setSubject(subject);

			// 3. Convert the AI's newline characters (\n) to HTML line breaks (<br>)
			String formattedMessage = messageBody.replace("\n", "<br>");

			// 4. Wrap the message in a modern HTML/CSS template
			String htmlContent = buildAiResHtmlTemplate(subject, formattedMessage);

			// 5. Set the text and mark it as HTML (true)
			helper.setText(htmlContent, true);

			// 6. Send the email
			mailSender.send(mimeMessage);
			return true;

		} catch (MessagingException e) {
			log.error("Failed to send AI Response Gmail", e);
			return false;
		}
	}

	public void sendFOSMISNotice(String title,
			LocalDateTime publishedAt,
			String link,
			String email) {
		try {
			MimeMessage mimeMessage = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");

			helper.setTo(email);
			helper.setSubject("📢 New FOSMIS Notice: " + title);

			String html = """
					<div style="font-family: Arial, sans-serif; max-width: 600px; margin: auto;
					            border: 1px solid #e0e0e0; border-radius: 8px; overflow: hidden;">
					  <div style="background-color: #1a3c6e; color: #ffffff; padding: 16px 24px;">
					    <h2 style="margin: 0; font-size: 18px;">New FOSMIS Notice</h2>
					  </div>
					  <div style="padding: 20px 24px;">
					    <p style="font-size: 16px; font-weight: bold; color: #1a1a1a; margin: 0 0 12px;">
					      %s
					    </p>
					    <p style="font-size: 14px; color: #555555; margin: 0 0 20px;">
					      Published: %s
					    </p>
					    <a href="%s" style="display: inline-block; background-color: #1a3c6e; color: #ffffff;
					              text-decoration: none; padding: 10px 20px; border-radius: 5px; font-size: 14px;">
					      View Notice
					    </a>
					  </div>
					  <div style="background-color: #f5f5f5; padding: 12px 24px; font-size: 12px; color: #999999;">
					    Faculty of Science, University of Ruhuna — FOSMIS
					  </div>
					</div>
					""".formatted(title, publishedAt, link);

			helper.setText(html, true); // true = isHtml

			mailSender.send(mimeMessage);
		} catch (MessagingException e) {
			throw new RuntimeException("Failed to send notice email", e);
		}
	}

	public void sendOtpEmail(String toEmail, String otp) {
		try {
			MimeMessage mimeMessage = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");

			helper.setTo(toEmail);
			helper.setSubject("Security Verification: Your JobNotifier OTP");

			String html = """
					<div style="font-family: Arial, sans-serif; max-width: 600px; margin: auto;
					            border: 1px solid #e0e0e0; border-radius: 8px; overflow: hidden;">
					  <div style="background-color: #059669; color: #ffffff; padding: 16px 24px; text-align: center;">
					    <h2 style="margin: 0; font-size: 20px;">Verification Required</h2>
					  </div>
					  <div style="padding: 30px 24px; text-align: center;">
					    <p style="font-size: 16px; color: #333333; margin: 0 0 20px;">
					      Please use the following 6-digit code to verify your email address. This code will expire in 15 minutes.
					    </p>
					    <div style="display: inline-block; background-color: #f3f4f6; color: #111827;
					                font-size: 32px; font-weight: bold; letter-spacing: 4px; padding: 15px 30px;
					                border-radius: 8px; border: 1px dashed #059669; margin-bottom: 20px;">
					      %s
					    </div>
					    <p style="font-size: 12px; color: #999999; margin: 0;">
					      If you did not request this, please ignore this email.
					    </p>
					  </div>
					</div>
					"""
					.formatted(otp);

			helper.setText(html, true);
			mailSender.send(mimeMessage);
		} catch (MessagingException e) {
			throw new RuntimeException("Failed to send OTP email", e);
		}
	}

	// A helper method to keep your HTML clean and separate from the sending logic
	private String buildAiResHtmlTemplate(String title, String body) {
		// Note: We use inline CSS because email clients (like Gmail) strip out external
		// stylesheets
		return """
				<!DOCTYPE html>
				<html>
				<body style="margin: 0; padding: 20px; background-color: #f3f4f6; font-family: 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;">
				    <table role="presentation" style="width: 100%%; max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05); border-collapse: collapse;">

				        <!-- Header -->
				        <tr>
				            <td style="background-color: #4f46e5; padding: 30px; text-align: center;">
				                <h2 style="color: #ffffff; margin: 0; font-size: 24px; font-weight: 600;">%s</h2>
				            </td>
				        </tr>

				        <!-- Body Content -->
				        <tr>
				            <td style="padding: 40px 30px; color: #374151; font-size: 16px; line-height: 1.8;">
				                %s
				            </td>
				        </tr>

				        <!-- Footer -->
				        <tr>
				            <td style="background-color: #f9fafb; padding: 20px; text-align: center; border-top: 1px solid #e5e7eb;">
				                <p style="color: #6b7280; font-size: 13px; margin: 0;">
				                    ✨ Generated by Kuna Deyyo ✨<br>
				                    Kuna Dewale , Sri Lanka
				                </p>
				            </td>
				        </tr>
				    </table>
				</body>
				</html>
				"""
				.formatted(title, body);
	}

	public void sendNewJobPostingsNotification(String website,String toEmail, List<JobDTO> newJobDTOS) {
		validateJobNotificationInput(toEmail, newJobDTOS);

		long distinctCompanies = newJobDTOS.stream()
				.map(JobDTO::getCompanyName)
				.filter(Objects::nonNull)
				.distinct()
				.count();
		String subject = distinctCompanies == 1
				?website+" - "+ newJobDTOS.get(0).getCompanyName()
				:website+" - "+ newJobDTOS.size() + " New Jobs";
		String htmlBody = buildJobPostingsHtml(newJobDTOS);

		try {
			MimeMessage mimeMessage = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
			helper.setTo(toEmail.trim());
			helper.setSubject(subject);
			helper.setText(htmlBody, true);
			mailSender.send(mimeMessage);
			System.out.println("New Job Postings Notification Sent to " + toEmail);
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

	private void validateJobNotificationInput(String toEmail, List<JobDTO> newJobDTOS) {
		if (isBlank(toEmail)) {
			throw new IllegalArgumentException("Recipient email is required");
		}
		if (Objects.isNull(newJobDTOS) || newJobDTOS.isEmpty()) {
			throw new IllegalArgumentException("Job list must contain at least one item");
		}
	}

	private String buildJobPostingsHtml(List<JobDTO> jobDTOS) {
		StringBuilder html = new StringBuilder();
		html.append("<!DOCTYPE html><html><head><meta charset='UTF-8'></head>");
		html.append(
				"<body style='margin:0;padding:0;background:#f3f6fb;font-family:Segoe UI,Arial,sans-serif;color:#1f2937;'>");
		html.append("<div style='max-width:680px;margin:24px auto;padding:20px;'>");
		html.append(
				"<div style='background:linear-gradient(120deg,#0f4c81,#2a9d8f);color:#ffffff;border-radius:16px;padding:20px 22px;'>");
		html.append("<h2 style='margin:0;font-size:24px;'>New Job Opportunities</h2>");
		html.append(
				"<p style='margin:8px 0 0 0;font-size:14px;opacity:0.95;'>Fresh listings matched from your sources.</p>");
		html.append("</div>");

		for (JobDTO jobDTO : jobDTOS) {
			html.append(
					"<div style='background:#ffffff;border:1px solid #dbe3ef;border-radius:14px;padding:16px;margin-top:14px;'>");
			html.append("<p style='margin:0 0 8px 0;color:#6b7280;font-size:12px;letter-spacing:0.4px;'>POSITION</p>");
			html.append("<h3 style='margin:0 0 14px 0;font-size:20px;line-height:1.3;color:#111827;'>")
					.append(escapeHtml(jobDTO.getPosition()))
					.append("</h3>");
			html.append("<p style='margin:0 0 8px 0;color:#6b7280;font-size:12px;letter-spacing:0.4px;'>COMPANY</p>");
			html.append("<p style='margin:0 0 14px 0;font-size:16px;color:#1f2937;'>")
					.append(escapeHtml(jobDTO.getCompanyName()))
					.append("</p>");
			html.append("<p style='margin:0 0 8px 0;color:#6b7280;font-size:12px;letter-spacing:0.4px;'>SOURCE</p>");
			html.append("<a href='")
					.append(escapeHtml(jobDTO.getSource()))
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
