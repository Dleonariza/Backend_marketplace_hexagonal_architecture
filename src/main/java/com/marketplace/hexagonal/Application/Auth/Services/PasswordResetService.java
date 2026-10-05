package com.marketplace.hexagonal.Application.Auth.Services;

import com.marketplace.hexagonal.Application.User.Services.UserService;
import com.marketplace.hexagonal.Domain.Auth.Models.PasswordResetToken;
import com.marketplace.hexagonal.Domain.Auth.Ports.In.ForgotPasswordUseCase;
import com.marketplace.hexagonal.Domain.Auth.Ports.In.ResetPasswordUseCase;
import com.marketplace.hexagonal.Domain.Auth.Ports.Out.PasswordResetTokenRepositoryPort;
import com.marketplace.hexagonal.Domain.Notification.Ports.Out.EmailNotificationPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetService implements ForgotPasswordUseCase, ResetPasswordUseCase {

    private static final int CODE_EXPIRATION_MINUTES = 15;

    private final PasswordResetTokenRepositoryPort resetTokenRepository;
    private final EmailNotificationPort emailNotificationPort;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void forgotPassword(String email) {
        String normalizedEmail = email.toLowerCase().strip();

        userService.findByEmail(normalizedEmail).ifPresent(user -> {
            String code = generateCode();

            resetTokenRepository.findByEmail(normalizedEmail)
                    .ifPresent(resetTokenRepository::delete);

            resetTokenRepository.save(PasswordResetToken.builder()
                    .email(normalizedEmail)
                    .codeHash(hash(code))
                    .expiresAt(Instant.now().plus(CODE_EXPIRATION_MINUTES, ChronoUnit.MINUTES))
                    .build());

            emailNotificationPort.sendPasswordResetCode(normalizedEmail, user.getName(), code);
        });
    }

    @Override
    @Transactional
    public void resetPassword(String email, String code, String newPassword) {
        String normalizedEmail = email.toLowerCase().strip();

        PasswordResetToken token = resetTokenRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No password reset was requested for this email."));

        if (token.isExpired()) {
            resetTokenRepository.delete(token);
            throw new IllegalArgumentException("The code has expired. Please request a new one.");
        }

        if (!token.getCodeHash().equals(hash(code))) {
            throw new IllegalArgumentException("Incorrect reset code.");
        }

        userService.updatePassword(normalizedEmail, passwordEncoder.encode(newPassword));
        resetTokenRepository.delete(token);

        log.info("Password reset successful for: {}", normalizedEmail);
    }

    private String generateCode() {
        SecureRandom random = new SecureRandom();
        return String.valueOf(100_000 + random.nextInt(900_000));
    }

    private String hash(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encoded = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(encoded);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
