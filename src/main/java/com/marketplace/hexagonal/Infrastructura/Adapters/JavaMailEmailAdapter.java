package com.marketplace.hexagonal.Infrastructura.Adapters;

import com.marketplace.hexagonal.Domain.Notification.Ports.Out.EmailNotificationPort;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JavaMailEmailAdapter implements EmailNotificationPort {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Async
    @Override
    public void sendVerificationCode(String toEmail, String userName, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Verification code - Marketplace");
            helper.setText(buildVerificationTemplate(userName, code), true);
            mailSender.send(message);
            log.info("Verification email sent to: {}", toEmail);
        } catch (MessagingException e) {
            log.error("Failed to send verification email to {}: {}", toEmail, e.getMessage());
            throw new IllegalStateException("Verification email could not be sent.", e);
        }
    }

    @Async
    @Override
    public void sendPasswordResetCode(String toEmail, String userName, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Password reset code - Marketplace");
            helper.setText(buildPasswordResetTemplate(userName, code), true);
            mailSender.send(message);
            log.info("Password reset email sent to: {}", toEmail);
        } catch (MessagingException e) {
            log.error("Failed to send password reset email to {}: {}", toEmail, e.getMessage());
            throw new IllegalStateException("Password reset email could not be sent.", e);
        }
    }

    private String buildVerificationTemplate(String userName, String code) {
        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                </head>
                <body style="margin:0;padding:0;background-color:#f4f4f4;font-family:Arial,sans-serif;">
                    <table width="100%%" cellpadding="0" cellspacing="0">
                        <tr>
                            <td align="center" style="padding:40px 0;">
                                <table width="500" cellpadding="0" cellspacing="0"
                                       style="background:#ffffff;border-radius:8px;overflow:hidden;
                                              box-shadow:0 2px 8px rgba(0,0,0,0.1);">
                                    <tr>
                                        <td style="background:#1a1a2e;padding:32px;text-align:center;">
                                            <h1 style="color:#ffffff;margin:0;font-size:24px;
                                                       letter-spacing:1px;">Marketplace</h1>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style="padding:40px 32px;">
                                            <p style="color:#333333;font-size:16px;margin:0 0 16px;">
                                                Hello, <strong>%s</strong>
                                            </p>
                                            <p style="color:#555555;font-size:14px;margin:0 0 32px;">
                                                Use the following code to verify your email and complete
                                                your registration. This code expires in
                                                <strong>15 minutes</strong>.
                                            </p>
                                            <div style="background:#f0f4ff;border:2px dashed #4a6cf7;
                                                        border-radius:8px;padding:24px;text-align:center;
                                                        margin:0 0 32px;">
                                                <span style="font-size:42px;font-weight:bold;
                                                             letter-spacing:12px;color:#1a1a2e;">%s</span>
                                            </div>
                                            <p style="color:#888888;font-size:12px;margin:0;">
                                                If you didn't request this code, ignore this message.
                                                No one else can complete your registration without it.
                                            </p>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style="background:#f9f9f9;padding:20px 32px;
                                                   border-top:1px solid #eeeeee;text-align:center;">
                                            <p style="color:#aaaaaa;font-size:12px;margin:0;">
                                                &copy; 2026 Marketplace. All rights reserved.
                                            </p>
                                        </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                    </table>
                </body>
                </html>
                """.formatted(userName, code);
    }

    private String buildPasswordResetTemplate(String userName, String code) {
        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                </head>
                <body style="margin:0;padding:0;background-color:#f4f4f4;font-family:Arial,sans-serif;">
                    <table width="100%%" cellpadding="0" cellspacing="0">
                        <tr>
                            <td align="center" style="padding:40px 0;">
                                <table width="500" cellpadding="0" cellspacing="0"
                                       style="background:#ffffff;border-radius:8px;overflow:hidden;
                                              box-shadow:0 2px 8px rgba(0,0,0,0.1);">
                                    <tr>
                                        <td style="background:#1a1a2e;padding:32px;text-align:center;">
                                            <h1 style="color:#ffffff;margin:0;font-size:24px;
                                                       letter-spacing:1px;">Marketplace</h1>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style="padding:40px 32px;">
                                            <p style="color:#333333;font-size:16px;margin:0 0 16px;">
                                                Hello, <strong>%s</strong>
                                            </p>
                                            <p style="color:#555555;font-size:14px;margin:0 0 32px;">
                                                We received a request to reset your password.
                                                Use the code below to continue. It expires in
                                                <strong>15 minutes</strong>.
                                            </p>
                                            <div style="background:#fff4f4;border:2px dashed #e74c3c;
                                                        border-radius:8px;padding:24px;text-align:center;
                                                        margin:0 0 32px;">
                                                <span style="font-size:42px;font-weight:bold;
                                                             letter-spacing:12px;color:#1a1a2e;">%s</span>
                                            </div>
                                            <p style="color:#888888;font-size:12px;margin:0;">
                                                If you did not request a password reset, ignore this email.
                                                Your password will remain unchanged.
                                            </p>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style="background:#f9f9f9;padding:20px 32px;
                                                   border-top:1px solid #eeeeee;text-align:center;">
                                            <p style="color:#aaaaaa;font-size:12px;margin:0;">
                                                &copy; 2026 Marketplace. All rights reserved.
                                            </p>
                                        </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                    </table>
                </body>
                </html>
                """.formatted(userName, code);
    }
}
