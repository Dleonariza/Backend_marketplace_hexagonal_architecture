package com.marketplace.hexagonal.Application.Auth.Services;

import com.marketplace.hexagonal.Application.User.Services.UserService;
import com.marketplace.hexagonal.Domain.Auth.Models.PasswordResetToken;
import com.marketplace.hexagonal.Domain.Auth.Ports.Out.PasswordResetTokenRepositoryPort;
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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {

    @Mock private PasswordResetTokenRepositoryPort resetTokenRepository;
    @Mock private EmailNotificationPort emailNotificationPort;
    @Mock private UserService userService;
    @Mock private PasswordEncoder passwordEncoder;
    @InjectMocks
    private PasswordResetService passwordResetService;

    private final String EMAIL = "user@test.com";

    @Test
    void forgotPassword_whenEmailExists_shouldSaveTokenAndSendEmail() {
        UserModel user = UserModel.create(EMAIL, "user1", "pass", "User", null, "3001234567", List.of());
        when(userService.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(resetTokenRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());
        when(resetTokenRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        passwordResetService.forgotPassword(EMAIL);

        verify(resetTokenRepository).save(any(PasswordResetToken.class));
        verify(emailNotificationPort).sendPasswordResetCode(eq(EMAIL), eq("User"), anyString());
    }

    @Test
    void forgotPassword_whenEmailDoesNotExist_shouldNotRevealInformation() {
        when(userService.findByEmail(anyString())).thenReturn(Optional.empty());

        passwordResetService.forgotPassword("unknown@test.com");

        verify(resetTokenRepository, never()).save(any());
        verify(emailNotificationPort, never()).sendPasswordResetCode(any(), any(), any());
    }

    @Test
    void forgotPassword_whenTokenAlreadyExists_shouldDeleteOldAndSaveNew() {
        UserModel user = UserModel.create(EMAIL, "user1", "pass", "User", null, "3001234567", List.of());
        PasswordResetToken existing = buildToken(false);

        when(userService.findByEmail(EMAIL)).thenReturn(Optional.of(user));
        when(resetTokenRepository.findByEmail(EMAIL)).thenReturn(Optional.of(existing));
        when(resetTokenRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        passwordResetService.forgotPassword(EMAIL);

        verify(resetTokenRepository).delete(existing);
        verify(resetTokenRepository).save(any(PasswordResetToken.class));
    }

    @Test
    void resetPassword_whenNoTokenExists_shouldThrow() {
        when(resetTokenRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> passwordResetService.resetPassword(EMAIL, "123456", "newPass"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No password reset");
    }

    @Test
    void resetPassword_whenTokenExpired_shouldDeleteAndThrow() {
        PasswordResetToken expired = buildToken(true);
        when(resetTokenRepository.findByEmail(EMAIL)).thenReturn(Optional.of(expired));

        assertThatThrownBy(() -> passwordResetService.resetPassword(EMAIL, "123456", "newPass"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("expired");
        verify(resetTokenRepository).delete(expired);
    }

    @Test
    void resetPassword_whenCodeIncorrect_shouldThrow() {
        PasswordResetToken token = buildToken(false);
        when(resetTokenRepository.findByEmail(EMAIL)).thenReturn(Optional.of(token));

        assertThatThrownBy(() -> passwordResetService.resetPassword(EMAIL, "wrong-code", "newPass"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Incorrect");
    }

    @Test
    void resetPassword_whenCodeValid_shouldUpdatePasswordAndDeleteToken() {
        String knownCode = "654321";
        String codeHash = sha256(knownCode);

        PasswordResetToken token = PasswordResetToken.builder()
                .email(EMAIL)
                .codeHash(codeHash)
                .expiresAt(Instant.now().plus(15, ChronoUnit.MINUTES))
                .build();

        when(resetTokenRepository.findByEmail(EMAIL)).thenReturn(Optional.of(token));
        when(passwordEncoder.encode("newSecurePass")).thenReturn("encoded-new-pass");

        passwordResetService.resetPassword(EMAIL, knownCode, "newSecurePass");

        verify(userService).updatePassword(EMAIL, "encoded-new-pass");
        verify(resetTokenRepository).delete(token);
    }

    private PasswordResetToken buildToken(boolean expired) {
        return PasswordResetToken.builder()
                .email(EMAIL)
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
