package com.marketplace.hexagonal.Domain.Seller;

import com.marketplace.hexagonal.Domain.Seller.Exceptions.InvalidSellerStatusTransitionException;
import com.marketplace.hexagonal.Domain.Seller.Models.*;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class SellerProfileTest {

    private static final UserId USER_ID = UserId.of("550e8400-e29b-41d4-a716-446655440000");
    private static final UserId ADMIN_ID = UserId.of("660e8400-e29b-41d4-a716-446655440001");

    @Test
    void create_shouldGenerateIdAndSetPendingStatus() {
        SellerProfile profile = buildProfile();

        assertThat(profile.getId()).isNotNull();
        assertThat(profile.getStatus()).isEqualTo(SellerStatus.PENDING_APPROVAL);
        assertThat(profile.getAppliedAt()).isNotNull();
        assertThat(profile.getStoreName()).isEqualTo("Mi Tienda");
    }

    @Test
    void approve_whenPending_shouldSetActiveAndTimestamps() {
        SellerProfile profile = buildProfile();

        profile.approve(ADMIN_ID);

        assertThat(profile.getStatus()).isEqualTo(SellerStatus.ACTIVE);
        assertThat(profile.getReviewedBy()).isEqualTo(ADMIN_ID);
        assertThat(profile.getReviewedAt()).isNotNull();
        assertThat(profile.getRejectionReason()).isNull();
    }

    @Test
    void approve_whenNotPending_shouldThrow() {
        SellerProfile profile = buildProfile();
        profile.approve(ADMIN_ID);

        assertThatThrownBy(() -> profile.approve(ADMIN_ID))
                .isInstanceOf(InvalidSellerStatusTransitionException.class);
    }

    @Test
    void reject_whenPending_shouldSetRejectedWithReason() {
        SellerProfile profile = buildProfile();

        profile.reject(ADMIN_ID, "Datos incompletos");

        assertThat(profile.getStatus()).isEqualTo(SellerStatus.REJECTED);
        assertThat(profile.getRejectionReason()).isEqualTo("Datos incompletos");
        assertThat(profile.getReviewedBy()).isEqualTo(ADMIN_ID);
    }

    @Test
    void reject_whenNotPending_shouldThrow() {
        SellerProfile profile = buildProfile();
        profile.approve(ADMIN_ID);

        assertThatThrownBy(() -> profile.reject(ADMIN_ID, "reason"))
                .isInstanceOf(InvalidSellerStatusTransitionException.class);
    }

    @Test
    void suspend_whenActive_shouldSetSuspendedWithReason() {
        SellerProfile profile = buildProfile();
        profile.approve(ADMIN_ID);

        profile.suspend(ADMIN_ID, "Violación de políticas");

        assertThat(profile.getStatus()).isEqualTo(SellerStatus.SUSPENDED);
        assertThat(profile.getSuspensionReason()).isEqualTo("Violación de políticas");
    }

    @Test
    void suspend_whenNotActive_shouldThrow() {
        SellerProfile profile = buildProfile();

        assertThatThrownBy(() -> profile.suspend(ADMIN_ID, "reason"))
                .isInstanceOf(InvalidSellerStatusTransitionException.class);
    }

    @Test
    void reactivate_whenSuspended_shouldSetActive() {
        SellerProfile profile = buildProfile();
        profile.approve(ADMIN_ID);
        profile.suspend(ADMIN_ID, "policy violation");

        profile.reactivate(ADMIN_ID);

        assertThat(profile.getStatus()).isEqualTo(SellerStatus.ACTIVE);
        assertThat(profile.getSuspensionReason()).isNull();
    }

    @Test
    void reactivate_whenNotSuspended_shouldThrow() {
        SellerProfile profile = buildProfile();
        profile.approve(ADMIN_ID);

        assertThatThrownBy(() -> profile.reactivate(ADMIN_ID))
                .isInstanceOf(InvalidSellerStatusTransitionException.class);
    }

    @Test
    void reapply_whenRejected_shouldResetToPendingWithNewData() {
        SellerProfile profile = buildProfile();
        profile.reject(ADMIN_ID, "bad data");

        profile.reapply("Nueva Tienda", "Nueva descripción", "Electronics",
                "nuevo@email.com", "3009999999", null,
                new IdentityDocument(DocumentType.NIT, "900111222"),
                buildBankingInfo());

        assertThat(profile.getStatus()).isEqualTo(SellerStatus.PENDING_APPROVAL);
        assertThat(profile.getStoreName()).isEqualTo("Nueva Tienda");
        assertThat(profile.getRejectionReason()).isNull();
        assertThat(profile.getReviewedAt()).isNull();
    }

    @Test
    void reapply_whenNotRejected_shouldThrow() {
        SellerProfile profile = buildProfile();

        assertThatThrownBy(() -> profile.reapply(
                "Nueva Tienda", "desc", "cat",
                "e@m.com", "3001111111", null,
                new IdentityDocument(DocumentType.CC, "12345678"),
                buildBankingInfo()))
                .isInstanceOf(InvalidSellerStatusTransitionException.class);
    }

    @Test
    void cancel_whenPending_shouldSetCancelledStatus() {
        SellerProfile profile = buildProfile();

        profile.cancel();

        assertThat(profile.getStatus()).isEqualTo(SellerStatus.CANCELLED);
    }

    @Test
    void cancel_whenNotPending_shouldThrow() {
        SellerProfile profile = buildProfile();
        profile.approve(ADMIN_ID);

        assertThatThrownBy(() -> profile.cancel())
                .isInstanceOf(InvalidSellerStatusTransitionException.class);
    }

    @Test
    void ban_whenAnyNonBannedStatus_shouldSetBannedStatus() {
        SellerProfile profile = buildProfile();

        profile.ban(ADMIN_ID, "Actividad fraudulenta");

        assertThat(profile.getStatus()).isEqualTo(SellerStatus.BANNED);
        assertThat(profile.getSuspensionReason()).isEqualTo("Actividad fraudulenta");
        assertThat(profile.getReviewedBy()).isEqualTo(ADMIN_ID);
    }

    @Test
    void ban_whenAlreadyBanned_shouldThrow() {
        SellerProfile profile = buildProfile();
        profile.ban(ADMIN_ID, "first ban");

        assertThatThrownBy(() -> profile.ban(ADMIN_ID, "second ban"))
                .isInstanceOf(InvalidSellerStatusTransitionException.class);
    }

    @Test
    void updateStoreInfo_whenActive_shouldUpdateFields() {
        SellerProfile profile = buildProfile();
        profile.approve(ADMIN_ID);

        profile.updateStoreInfo("Nueva Tienda", "Nueva descripcion", "Fashion",
                "nuevo@email.com", "3009999999", "http://logo.com/new.png");

        assertThat(profile.getStoreName()).isEqualTo("Nueva Tienda");
        assertThat(profile.getStoreDescription()).isEqualTo("Nueva descripcion");
        assertThat(profile.getMainCategory()).isEqualTo("Fashion");
        assertThat(profile.getStatus()).isEqualTo(SellerStatus.ACTIVE);
    }

    @Test
    void updateStoreInfo_whenSuspended_shouldBeAllowed() {
        SellerProfile profile = buildProfile();
        profile.approve(ADMIN_ID);
        profile.suspend(ADMIN_ID, "policy");

        profile.updateStoreInfo("Tienda Revisada", "desc", "cat",
                "e@m.com", "300", null);

        assertThat(profile.getStoreName()).isEqualTo("Tienda Revisada");
        assertThat(profile.getStatus()).isEqualTo(SellerStatus.SUSPENDED);
    }

    @Test
    void updateStoreInfo_whenPending_shouldThrow() {
        SellerProfile profile = buildProfile();

        assertThatThrownBy(() -> profile.updateStoreInfo(
                "name", "desc", "cat", "e@m.com", "300", null))
                .isInstanceOf(InvalidSellerStatusTransitionException.class);
    }

    @Test
    void reapply_whenCancelled_shouldResetToPending() {
        SellerProfile profile = buildProfile();
        profile.cancel();

        profile.reapply("Nueva Tienda", "desc", "cat",
                "e@m.com", "3009999999", null,
                new IdentityDocument(DocumentType.CC, "12345678"),
                buildBankingInfo());

        assertThat(profile.getStatus()).isEqualTo(SellerStatus.PENDING_APPROVAL);
        assertThat(profile.getStoreName()).isEqualTo("Nueva Tienda");
    }

    private SellerProfile buildProfile() {
        return SellerProfile.create(
                USER_ID,
                "Mi Tienda",
                "Tienda de tecnología",
                "Electronics",
                "contacto@mitienda.com",
                "3001234567",
                null,
                new IdentityDocument(DocumentType.CC, "12345678"),
                buildBankingInfo()
        );
    }

    private BankingInfo buildBankingInfo() {
        return new BankingInfo(
                "Bancolombia", "12345678901", AccountType.SAVINGS,
                "John Doe", "12345678");
    }
}
