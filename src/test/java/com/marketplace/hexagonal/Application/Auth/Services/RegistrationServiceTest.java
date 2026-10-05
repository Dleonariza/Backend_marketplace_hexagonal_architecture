package com.marketplace.hexagonal.Application.Auth.Services;

import com.marketplace.hexagonal.Application.User.Services.UserService;
import com.marketplace.hexagonal.Domain.Auth.Models.PendingRegistration;
import com.marketplace.hexagonal.Domain.Auth.Ports.Out.PendingRegistrationRepositoryPort;
import com.marketplace.hexagonal.Domain.Notification.Ports.Out.EmailNotificationPort;
import com.marketplace.hexagonal.Domain.User.Models.UserModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {

    @Mock private PendingRegistrationRepositoryPort pendingRepository;
    @Mock private EmailNotificationPort emailNotificationPort;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private UserService userService;
    @InjectMocks
    private RegistrationService registrationService;

    @Test
    void preRegister_whenEmailAlreadyRegistered_throwsIllegalArgumentException() {
        when(userService.existsByEmail("user@test.com")).thenReturn(true);

        assertThatThrownBy(() ->
                registrationService.preRegister("user@test.com", "user1", "pass", "User", "3001234567"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already registered");
    }

    @Test
    void preRegister_whenUsernameAlreadyTaken_throwsIllegalArgumentException() {
        when(userService.existsByEmail(anyString())).thenReturn(false);
        when(userService.existsByUserName("existingUser")).thenReturn(true);

        assertThatThrownBy(() ->
                registrationService.preRegister("new@test.com", "existingUser", "pass", "User", "3001234567"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already in use");
    }

    @Test
    void preRegister_whenValidData_savePendingAndSendVerificationEmail() {
        when(userService.existsByEmail(anyString())).thenReturn(false);
        when(userService.existsByUserName(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashed-password");
        when(pendingRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(pendingRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        String result = registrationService.preRegister(
                "NEW@TEST.COM", "newUser", "password", "New User", "3001234567");

        assertThat(result).isEqualTo("new@test.com");
        verify(pendingRepository).save(any(PendingRegistration.class));
        verify(emailNotificationPort).sendVerificationCode(eq("new@test.com"), eq("New User"), anyString());
    }

    @Test
    void preRegister_whenPendingAlreadyExists_shouldDeleteOldAndSaveNew() {
        PendingRegistration existing = buildPending(false);
        when(userService.existsByEmail(anyString())).thenReturn(false);
        when(userService.existsByUserName(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashed");
        when(pendingRepository.findByEmail(anyString())).thenReturn(Optional.of(existing));
        when(pendingRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        registrationService.preRegister("user@test.com", "user1", "pass", "User", "3001234567");

        verify(pendingRepository).delete(existing);
        verify(pendingRepository).save(any(PendingRegistration.class));
    }

    @Test
    void verifyEmail_whenNoPendingRegistration_shouldThrow() {
        when(pendingRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> registrationService.verifyEmail("user@test.com", "123456"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No pending registration");
    }

    @Test
    void verifyEmail_whenCodeExpired_shouldDeleteAndThrow() {
        PendingRegistration expired = buildPending(true);
        when(pendingRepository.findByEmail(anyString())).thenReturn(Optional.of(expired));

        assertThatThrownBy(() -> registrationService.verifyEmail("user@test.com", "123456"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("expired");
        verify(pendingRepository).delete(expired);
    }

    @Test
    void verifyEmail_whenCodeIncorrect_shouldThrow() {
        PendingRegistration pending = buildPending(false);
        when(pendingRepository.findByEmail(anyString())).thenReturn(Optional.of(pending));

        assertThatThrownBy(() -> registrationService.verifyEmail("user@test.com", "000000"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Incorrect");
    }

    @Test
    void verifyEmail_whenValidCode_shouldCreateUserAndDeletePending() {
        String knownCode = "123456";
        String codeHash = sha256(knownCode);

        PendingRegistration pending = PendingRegistration.builder()
                .email("user@test.com")
                .userName("user1")
                .passwordHash("hashed-pass")
                .name("User")
                .phone("3001234567")
                .codeHash(codeHash)
                .expiresAt(Instant.now().plus(15, ChronoUnit.MINUTES))
                .build();

        UserModel createdUser = UserModel.create(
                "user@test.com", "user1", "hashed-pass", "User", null, "3001234567", List.of());

        when(pendingRepository.findByEmail("user@test.com")).thenReturn(Optional.of(pending));
        when(userService.createUserPreEncoded(any())).thenReturn(createdUser);

        UserModel result = registrationService.verifyEmail("USER@TEST.COM", knownCode);

        assertThat(result.getEmail()).isEqualTo("user@test.com");
        verify(userService).createUserPreEncoded(any());
        verify(pendingRepository).delete(pending);
    }

    @Test
    void resendCode_whenNoPendingRegistration_shouldThrow() {
        when(pendingRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> registrationService.resendCode("unknown@test.com"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void resendCode_whenPendingExists_shouldUpdateCodeAndResendEmail() {
        PendingRegistration pending = buildPending(false);
        when(pendingRepository.findByEmail("user@test.com")).thenReturn(Optional.of(pending));
        when(pendingRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        registrationService.resendCode("user@test.com");

        verify(pendingRepository).save(pending);
        verify(emailNotificationPort).sendVerificationCode(eq("user@test.com"), anyString(), anyString());
    }

    private PendingRegistration buildPending(boolean expired) {
        return PendingRegistration.builder()
                .email("user@test.com")
                .userName("user1")
                .passwordHash("hashed-pass")
                .name("User")
                .phone("3001234567")
                .codeHash("some-hash")
                .expiresAt(expired
                        ? Instant.now().minus(1, ChronoUnit.HOURS)
                        : Instant.now().plus(15, ChronoUnit.MINUTES))
                .build();
    }

    private String sha256(String value) {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] encoded = digest.digest(value.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return java.util.HexFormat.of().formatHex(encoded);
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
