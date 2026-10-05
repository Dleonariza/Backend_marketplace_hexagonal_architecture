package com.marketplace.hexagonal.Application.Auth.Services;

import com.marketplace.hexagonal.Application.User.Services.UserService;
import com.marketplace.hexagonal.Domain.Auth.Models.PendingRegistration;
import com.marketplace.hexagonal.Domain.Auth.Ports.In.PreRegisterUseCase;
import com.marketplace.hexagonal.Domain.Auth.Ports.In.ResendCodeUseCase;
import com.marketplace.hexagonal.Domain.Auth.Ports.In.VerifyEmailUseCase;
import com.marketplace.hexagonal.Domain.Auth.Ports.Out.PendingRegistrationRepositoryPort;
import com.marketplace.hexagonal.Domain.Notification.Ports.Out.EmailNotificationPort;
import com.marketplace.hexagonal.Domain.User.Models.UserModel;
import lombok.RequiredArgsConstructor;
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
import java.util.List;

@Service
@RequiredArgsConstructor
public class RegistrationService implements PreRegisterUseCase, VerifyEmailUseCase, ResendCodeUseCase {

    private static final int CODE_EXPIRATION_MINUTES = 15;

    private final PendingRegistrationRepositoryPort pendingRepository;
    private final EmailNotificationPort emailNotificationPort;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    @Override
    @Transactional
    public String preRegister(String email, String userName, String rawPassword, String name, String phone) {
        String normalizedEmail = email.toLowerCase().strip();
        String normalizedUserName = userName.strip();

        if (userService.existsByEmail(normalizedEmail)) {
            throw new IllegalArgumentException("The email is already registered.");
        }
        if (userService.existsByUserName(normalizedUserName)) {
            throw new IllegalArgumentException("The username is already in use.");
        }

        String code = generateCode();
        String codeHash = hash(code);

        pendingRepository.findByEmail(normalizedEmail)
                .ifPresent(pendingRepository::delete);

        pendingRepository.save(PendingRegistration.builder()
                .email(normalizedEmail)
                .userName(normalizedUserName)
                .passwordHash(passwordEncoder.encode(rawPassword))
                .name(name)
                .phone(phone)
                .codeHash(codeHash)
                .expiresAt(Instant.now().plus(CODE_EXPIRATION_MINUTES, ChronoUnit.MINUTES))
                .build());

        emailNotificationPort.sendVerificationCode(normalizedEmail, name, code);

        return normalizedEmail;
    }

    @Override
    @Transactional
    public UserModel verifyEmail(String email, String code) {
        String normalizedEmail = email.toLowerCase().strip();

        PendingRegistration pending = pendingRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No pending registration for this email."));

        if (pending.isExpired()) {
            pendingRepository.delete(pending);
            throw new IllegalArgumentException("The code has expired. Request a new one.");
        }

        if (!pending.getCodeHash().equals(hash(code))) {
            throw new IllegalArgumentException("Incorrect verification code.");
        }

        UserModel user = UserModel.create(
                pending.getEmail(), pending.getUserName(), pending.getPasswordHash(),
                pending.getName(), null, pending.getPhone(), List.of()
        );

        UserModel saved = userService.createUserPreEncoded(user);
        pendingRepository.delete(pending);

        return saved;
    }

    @Override
    @Transactional
    public String resendCode(String email) {
        String normalizedEmail = email.toLowerCase().strip();

        PendingRegistration pending = pendingRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No pending registration for this email."));

        String code = generateCode();
        pending.setCodeHash(hash(code));
        pending.setExpiresAt(Instant.now().plus(CODE_EXPIRATION_MINUTES, ChronoUnit.MINUTES));
        pendingRepository.save(pending);

        emailNotificationPort.sendVerificationCode(normalizedEmail, pending.getName(), code);

        return normalizedEmail;
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
