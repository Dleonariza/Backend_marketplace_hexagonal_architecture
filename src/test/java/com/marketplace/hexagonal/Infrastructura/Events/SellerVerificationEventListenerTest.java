package com.marketplace.hexagonal.Infrastructura.Events;

import com.marketplace.hexagonal.Domain.Seller.Events.SellerVerificationCompletedEvent;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerStatus;
import com.marketplace.hexagonal.Domain.User.Models.UserModel;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import com.marketplace.hexagonal.Domain.User.Ports.Out.UserRepositoryPort;
import com.marketplace.hexagonal.Infrastructura.Adapters.Websocket.SellerNotificationBroadcastService;
import com.marketplace.hexagonal.Infrastructura.DTO.Notification.SellerVerificationNotificationDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SellerVerificationEventListenerTest {

    @Mock UserRepositoryPort userRepo;
    @Mock SellerNotificationBroadcastService broadcastService;
    SellerVerificationEventListener listener;

    private static final UserId SELLER_USER_ID = UserId.of("550e8400-e29b-41d4-a716-446655440000");
    private static final String SELLER_EMAIL = "seller@test.com";

    @BeforeEach
    void setUp() {
        listener = new SellerVerificationEventListener(userRepo, broadcastService);
    }

    @Test
    void onSellerVerificationCompleted_approved_broadcastsApprovedNotification() {
        UserModel seller = buildSeller();
        when(userRepo.findById(SELLER_USER_ID)).thenReturn(seller);

        listener.onSellerVerificationCompleted(new SellerVerificationCompletedEvent(
                SELLER_USER_ID, "Mi Tienda", SellerStatus.ACTIVE, null));

        verify(broadcastService).broadcastVerification(
                eq(SELLER_EMAIL),
                argThat(dto -> "VERIFICATION_APPROVED".equals(dto.type())
                        && "ACTIVE".equals(dto.status())
                        && dto.reason() == null));
    }

    @Test
    void onSellerVerificationCompleted_rejected_broadcastsRejectedNotification() {
        UserModel seller = buildSeller();
        when(userRepo.findById(SELLER_USER_ID)).thenReturn(seller);

        listener.onSellerVerificationCompleted(new SellerVerificationCompletedEvent(
                SELLER_USER_ID, "Mi Tienda", SellerStatus.REJECTED, "Documentos inválidos"));

        verify(broadcastService).broadcastVerification(
                eq(SELLER_EMAIL),
                argThat(dto -> "VERIFICATION_REJECTED".equals(dto.type())
                        && "REJECTED".equals(dto.status())
                        && "Documentos inválidos".equals(dto.reason())));
    }

    @Test
    void onSellerVerificationCompleted_broadcastError_doesNotThrow() {
        UserModel seller = buildSeller();
        when(userRepo.findById(SELLER_USER_ID)).thenReturn(seller);
        doThrow(new RuntimeException("WS unavailable"))
                .when(broadcastService).broadcastVerification(any(), any());

        listener.onSellerVerificationCompleted(new SellerVerificationCompletedEvent(
                SELLER_USER_ID, "Mi Tienda", SellerStatus.ACTIVE, null));

        verify(broadcastService).broadcastVerification(any(), any(SellerVerificationNotificationDTO.class));
    }

    private UserModel buildSeller() {
        return UserModel.create(SELLER_EMAIL, "seller1", "hash", "Seller Name",
                null, "3001234567", List.of());
    }
}
