package com.marketplace.hexagonal.Infrastructura.Events;

import com.marketplace.hexagonal.Domain.Product.Events.LowStockAlertEvent;
import com.marketplace.hexagonal.Domain.Product.Models.ProductId;
import com.marketplace.hexagonal.Domain.Product.Models.ProductVariantId;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfile;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerStatus;
import com.marketplace.hexagonal.Domain.Seller.Ports.Out.SellerProfileRepositoryPort;
import com.marketplace.hexagonal.Domain.User.Models.UserModel;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import com.marketplace.hexagonal.Domain.User.Ports.Out.UserRepositoryPort;
import com.marketplace.hexagonal.Infrastructura.Adapters.Websocket.SellerNotificationBroadcastService;
import com.marketplace.hexagonal.Infrastructura.DTO.Notification.LowStockAlertDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LowStockEventListenerTest {

    @Mock SellerProfileRepositoryPort sellerProfileRepo;
    @Mock UserRepositoryPort userRepo;
    @Mock SellerNotificationBroadcastService broadcastService;
    LowStockEventListener listener;

    private static final SellerProfileId SELLER_PROFILE_ID = SellerProfileId.of("770e8400-e29b-41d4-a716-446655440002");
    private static final UserId SELLER_USER_ID = UserId.of("550e8400-e29b-41d4-a716-446655440000");
    private static final String SELLER_EMAIL = "seller@test.com";
    private static final ProductId PRODUCT_ID = ProductId.of("aa000000-0000-0000-0000-000000000001");

    @BeforeEach
    void setUp() {
        listener = new LowStockEventListener(sellerProfileRepo, userRepo, broadcastService);
    }

    @Test
    void onLowStock_broadcastsAlertToSeller() {
        when(sellerProfileRepo.findById(SELLER_PROFILE_ID)).thenReturn(Optional.of(buildSellerProfile()));
        when(userRepo.findById(SELLER_USER_ID)).thenReturn(buildSeller());

        listener.onLowStock(buildEvent(3));

        verify(broadcastService).broadcastLowStock(
                eq(SELLER_EMAIL),
                argThat(dto -> "LOW_STOCK_ALERT".equals(dto.type())
                        && dto.currentStock() == 3
                        && "SKU-001".equals(dto.sku())
                        && "Test Product".equals(dto.productName())));
    }

    @Test
    void onLowStock_stockAtThreshold_broadcastsAlert() {
        when(sellerProfileRepo.findById(SELLER_PROFILE_ID)).thenReturn(Optional.of(buildSellerProfile()));
        when(userRepo.findById(SELLER_USER_ID)).thenReturn(buildSeller());

        listener.onLowStock(buildEvent(LowStockAlertEvent.LOW_STOCK_THRESHOLD));

        verify(broadcastService).broadcastLowStock(any(), argThat(dto ->
                dto.currentStock() == LowStockAlertEvent.LOW_STOCK_THRESHOLD
                        && dto.threshold() == LowStockAlertEvent.LOW_STOCK_THRESHOLD));
    }

    @Test
    void onLowStock_broadcastError_doesNotThrow() {
        when(sellerProfileRepo.findById(SELLER_PROFILE_ID)).thenReturn(Optional.of(buildSellerProfile()));
        when(userRepo.findById(SELLER_USER_ID)).thenReturn(buildSeller());
        doThrow(new RuntimeException("WS unavailable"))
                .when(broadcastService).broadcastLowStock(any(), any());

        listener.onLowStock(buildEvent(2));

        verify(broadcastService).broadcastLowStock(any(), any(LowStockAlertDTO.class));
    }

    @Test
    void onLowStock_sellerProfileNotFound_doesNotThrowAndLogsError() {
        when(sellerProfileRepo.findById(SELLER_PROFILE_ID)).thenReturn(Optional.empty());

        listener.onLowStock(buildEvent(1));

        verifyNoInteractions(userRepo, broadcastService);
    }

    private LowStockAlertEvent buildEvent(int stock) {
        return new LowStockAlertEvent(
                PRODUCT_ID, "Test Product",
                ProductVariantId.generate(),
                "SKU-001", stock, SELLER_PROFILE_ID);
    }

    private SellerProfile buildSellerProfile() {
        return SellerProfile.builder()
                .id(SELLER_PROFILE_ID)
                .userId(SELLER_USER_ID)
                .storeName("Mi Tienda")
                .status(SellerStatus.ACTIVE)
                .build();
    }

    private UserModel buildSeller() {
        return UserModel.create(SELLER_EMAIL, "seller1", "hash", "Seller Name",
                null, "3001234567", List.of());
    }
}
