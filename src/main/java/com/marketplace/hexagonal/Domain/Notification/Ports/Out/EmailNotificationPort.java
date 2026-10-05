package com.marketplace.hexagonal.Domain.Notification.Ports.Out;

public interface EmailNotificationPort {
    void sendVerificationCode(String toEmail, String userName, String code);
    void sendPasswordResetCode(String toEmail, String userName, String code);
}
