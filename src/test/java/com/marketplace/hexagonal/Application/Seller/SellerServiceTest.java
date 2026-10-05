package com.marketplace.hexagonal.Application.Seller;

import com.marketplace.hexagonal.Application.Seller.UseCases.*;
import com.marketplace.hexagonal.Application.Seller.UseCases.BanSellerUseCaseImpl;
import com.marketplace.hexagonal.Application.Seller.UseCases.CancelSellerApplicationUseCaseImpl;
import com.marketplace.hexagonal.Application.Seller.UseCases.UpdateSellerProfileUseCaseImpl;
import com.marketplace.hexagonal.Application.Seller.Services.SellerService;
import com.marketplace.hexagonal.Domain.Seller.Exceptions.SellerAlreadyAppliedException;
import com.marketplace.hexagonal.Domain.Seller.Exceptions.SellerBannedException;
import com.marketplace.hexagonal.Domain.Seller.Exceptions.SellerProfileNotFoundException;
import com.marketplace.hexagonal.Domain.Seller.Models.*;
import com.marketplace.hexagonal.Domain.User.Models.RoleAndPermission.RoleEnum;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import com.marketplace.hexagonal.Domain.User.Ports.Out.UserRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import com.marketplace.hexagonal.Domain.Seller.Events.SellerVerificationCompletedEvent;
import com.marketplace.hexagonal.Domain.Seller.Ports.Out.SellerProfileRepositoryPort;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class SellerServiceTest {

    @Mock SellerProfileRepositoryPort sellerRepo;
    @Mock UserRepositoryPort userRepo;
    @Mock ApplicationEventPublisher eventPublisher;
    SellerService sellerService;

    private static final UserId USER_ID = UserId.of("550e8400-e29b-41d4-a716-446655440000");
    private static final UserId ADMIN_ID = UserId.of("660e8400-e29b-41d4-a716-446655440001");
    private static final SellerProfileId PROFILE_ID =
            SellerProfileId.of("770e8400-e29b-41d4-a716-446655440002");

    @BeforeEach
    void setUp() {
        sellerService = new SellerService(
                new ApplySellerUseCaseImpl(sellerRepo),
                new ApproveSellerUseCaseImpl(sellerRepo, userRepo),
                new RejectSellerUseCaseImpl(sellerRepo),
                new SuspendSellerUseCaseImpl(sellerRepo),
                new ReactivateSellerUseCaseImpl(sellerRepo),
                new CancelSellerApplicationUseCaseImpl(sellerRepo),
                new BanSellerUseCaseImpl(sellerRepo),
                new GetSellerProfileUseCaseImpl(sellerRepo),
                new GetSellerApplicationsUseCaseImpl(sellerRepo),
                new UpdateSellerProfileUseCaseImpl(sellerRepo),
                eventPublisher
        );
    }

    @Test
    void apply_whenNewUser_shouldCreatePendingProfile() {
        when(sellerRepo.findByUserId(USER_ID)).thenReturn(Optional.empty());
        when(sellerRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        SellerProfile result = applyWith(USER_ID);

        assertThat(result.getStatus()).isEqualTo(SellerStatus.PENDING_APPROVAL);
        assertThat(result.getUserId()).isEqualTo(USER_ID);
        verify(sellerRepo).save(any());
    }

    @Test
    void apply_whenRejectedUser_shouldReapplySuccessfully() {
        SellerProfile existing = buildPendingProfile();
        existing.reject(ADMIN_ID, "bad data");
        when(sellerRepo.findByUserId(USER_ID)).thenReturn(Optional.of(existing));
        when(sellerRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        SellerProfile result = applyWith(USER_ID);

        assertThat(result.getStatus()).isEqualTo(SellerStatus.PENDING_APPROVAL);
        verify(sellerRepo).save(any());
    }

    @Test
    void apply_whenPendingUser_shouldThrowAlreadyApplied() {
        when(sellerRepo.findByUserId(USER_ID)).thenReturn(Optional.of(buildPendingProfile()));

        assertThatThrownBy(() -> applyWith(USER_ID))
                .isInstanceOf(SellerAlreadyAppliedException.class);
    }

    @Test
    void apply_whenActiveUser_shouldThrowAlreadyApplied() {
        SellerProfile active = buildPendingProfile();
        active.approve(ADMIN_ID);
        when(sellerRepo.findByUserId(USER_ID)).thenReturn(Optional.of(active));

        assertThatThrownBy(() -> applyWith(USER_ID))
                .isInstanceOf(SellerAlreadyAppliedException.class);
    }

    @Test
    void approve_whenPending_shouldActivateAndGrantRole() {
        SellerProfile pending = buildPendingProfile();
        when(sellerRepo.findById(PROFILE_ID)).thenReturn(Optional.of(pending));
        when(sellerRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        SellerProfile result = sellerService.approve(PROFILE_ID, ADMIN_ID);

        assertThat(result.getStatus()).isEqualTo(SellerStatus.ACTIVE);
        verify(userRepo).addRole(pending.getUserId(), RoleEnum.SELLER);
    }

    @Test
    void approve_whenNotFound_shouldThrow() {
        when(sellerRepo.findById(PROFILE_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sellerService.approve(PROFILE_ID, ADMIN_ID))
                .isInstanceOf(SellerProfileNotFoundException.class);
    }

    @Test
    void reject_whenPending_shouldSetRejectedStatus() {
        SellerProfile pending = buildPendingProfile();
        when(sellerRepo.findById(PROFILE_ID)).thenReturn(Optional.of(pending));
        when(sellerRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        SellerProfile result = sellerService.reject(PROFILE_ID, ADMIN_ID, "Documentos inválidos");

        assertThat(result.getStatus()).isEqualTo(SellerStatus.REJECTED);
        assertThat(result.getRejectionReason()).isEqualTo("Documentos inválidos");
    }

    @Test
    void suspend_whenActive_shouldSetSuspendedStatus() {
        SellerProfile active = buildPendingProfile();
        active.approve(ADMIN_ID);
        when(sellerRepo.findById(PROFILE_ID)).thenReturn(Optional.of(active));
        when(sellerRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        SellerProfile result = sellerService.suspend(PROFILE_ID, ADMIN_ID, "Fraude detectado");

        assertThat(result.getStatus()).isEqualTo(SellerStatus.SUSPENDED);
        assertThat(result.getSuspensionReason()).isEqualTo("Fraude detectado");
    }

    @Test
    void reactivate_whenSuspended_shouldSetActiveStatus() {
        SellerProfile suspended = buildPendingProfile();
        suspended.approve(ADMIN_ID);
        suspended.suspend(ADMIN_ID, "policy");
        when(sellerRepo.findById(PROFILE_ID)).thenReturn(Optional.of(suspended));
        when(sellerRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        SellerProfile result = sellerService.reactivate(PROFILE_ID, ADMIN_ID);

        assertThat(result.getStatus()).isEqualTo(SellerStatus.ACTIVE);
    }

    @Test
    void cancelApplication_whenPending_shouldSetCancelledStatus() {
        SellerProfile pending = buildPendingProfile();
        when(sellerRepo.findByUserId(USER_ID)).thenReturn(Optional.of(pending));
        when(sellerRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        sellerService.cancelApplication(USER_ID);

        verify(sellerRepo).save(argThat(p -> p.getStatus() == SellerStatus.CANCELLED));
    }

    @Test
    void cancelApplication_whenNoProfile_shouldThrow() {
        when(sellerRepo.findByUserId(USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sellerService.cancelApplication(USER_ID))
                .isInstanceOf(SellerProfileNotFoundException.class);
    }

    @Test
    void ban_whenExists_shouldSetBannedStatus() {
        SellerProfile pending = buildPendingProfile();
        when(sellerRepo.findById(PROFILE_ID)).thenReturn(Optional.of(pending));
        when(sellerRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        SellerProfile result = sellerService.ban(PROFILE_ID, ADMIN_ID, "Actividad fraudulenta");

        assertThat(result.getStatus()).isEqualTo(SellerStatus.BANNED);
    }

    @Test
    void apply_whenBannedUser_shouldThrowSellerBanned() {
        SellerProfile banned = buildPendingProfile();
        banned.ban(ADMIN_ID, "fraude");
        when(sellerRepo.findByUserId(USER_ID)).thenReturn(Optional.of(banned));

        assertThatThrownBy(() -> applyWith(USER_ID))
                .isInstanceOf(SellerBannedException.class);
    }

    @Test
    void apply_whenCancelledUser_shouldAllowReapplication() {
        SellerProfile cancelled = buildPendingProfile();
        cancelled.cancel();
        when(sellerRepo.findByUserId(USER_ID)).thenReturn(Optional.of(cancelled));
        when(sellerRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        SellerProfile result = applyWith(USER_ID);

        assertThat(result.getStatus()).isEqualTo(SellerStatus.PENDING_APPROVAL);
    }

    @Test
    void updateProfile_whenActive_shouldReturnUpdatedProfile() {
        SellerProfile active = buildPendingProfile();
        active.approve(ADMIN_ID);
        when(sellerRepo.findByUserId(USER_ID)).thenReturn(Optional.of(active));
        when(sellerRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        SellerProfile result = sellerService.updateProfile(
                USER_ID, "Nueva Tienda", "Nueva desc", "Fashion",
                "nuevo@email.com", "3009999999", null);

        assertThat(result.getStoreName()).isEqualTo("Nueva Tienda");
        assertThat(result.getMainCategory()).isEqualTo("Fashion");
        assertThat(result.getStatus()).isEqualTo(SellerStatus.ACTIVE);
        verify(sellerRepo).save(any());
    }

    @Test
    void updateProfile_whenNoProfile_shouldThrow() {
        when(sellerRepo.findByUserId(USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sellerService.updateProfile(
                USER_ID, "name", "desc", "cat", "e@m.com", "300", null))
                .isInstanceOf(com.marketplace.hexagonal.Domain.Seller.Exceptions.SellerProfileNotFoundException.class);
    }

    @Test
    void getPendingApplications_shouldReturnOnlyPendingProfiles() {
        when(sellerRepo.findByStatus(SellerStatus.PENDING_APPROVAL))
                .thenReturn(List.of(buildPendingProfile()));

        List<SellerProfile> result = sellerService.getPendingApplications();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(SellerStatus.PENDING_APPROVAL);
    }

    @Test
    void findByUserId_whenExists_shouldReturnProfile() {
        when(sellerRepo.findByUserId(USER_ID)).thenReturn(Optional.of(buildPendingProfile()));

        Optional<SellerProfile> result = sellerService.findByUserId(USER_ID);

        assertThat(result).isPresent();
    }

    @Test
    void findByUserId_whenNotExists_shouldReturnEmpty() {
        when(sellerRepo.findByUserId(USER_ID)).thenReturn(Optional.empty());

        Optional<SellerProfile> result = sellerService.findByUserId(USER_ID);

        assertThat(result).isEmpty();
    }

    private SellerProfile applyWith(UserId userId) {
        return sellerService.apply(
                userId,
                "Mi Tienda", "Tienda de tecnología", "Electronics",
                "contacto@mitienda.com", "3001234567", null,
                new IdentityDocument(DocumentType.CC, "12345678"),
                new BankingInfo("Bancolombia", "12345678901", AccountType.SAVINGS,
                        "John Doe", "12345678"));
    }

    private SellerProfile buildPendingProfile() {
        return SellerProfile.create(
                USER_ID,
                "Mi Tienda", "Tienda de tecnología", "Electronics",
                "contacto@mitienda.com", "3001234567", null,
                new IdentityDocument(DocumentType.CC, "12345678"),
                new BankingInfo("Bancolombia", "12345678901", AccountType.SAVINGS,
                        "John Doe", "12345678"));
    }

    @Test
    void approve_publishesSellerVerificationCompletedEvent_withActiveStatus() {
        SellerProfile pending = buildPendingProfile();
        when(sellerRepo.findById(PROFILE_ID)).thenReturn(Optional.of(pending));
        when(sellerRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        sellerService.approve(PROFILE_ID, ADMIN_ID);

        verify(eventPublisher).publishEvent((Object) argThat(evt ->
                evt instanceof SellerVerificationCompletedEvent e
                        && e.newStatus() == SellerStatus.ACTIVE
                        && e.reason() == null
                        && e.sellerUserId().equals(USER_ID)));
    }

    @Test
    void reject_publishesSellerVerificationCompletedEvent_withRejectedStatus() {
        SellerProfile pending = buildPendingProfile();
        when(sellerRepo.findById(PROFILE_ID)).thenReturn(Optional.of(pending));
        when(sellerRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        sellerService.reject(PROFILE_ID, ADMIN_ID, "Documentos inválidos");

        verify(eventPublisher).publishEvent((Object) argThat(evt ->
                evt instanceof SellerVerificationCompletedEvent e
                        && e.newStatus() == SellerStatus.REJECTED
                        && "Documentos inválidos".equals(e.reason())
                        && e.sellerUserId().equals(USER_ID)));
    }

    @Test
    void approve_whenNotFound_doesNotPublishEvent() {
        when(sellerRepo.findById(PROFILE_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sellerService.approve(PROFILE_ID, ADMIN_ID))
                .isInstanceOf(SellerProfileNotFoundException.class);

        verifyNoInteractions(eventPublisher);
    }
}
