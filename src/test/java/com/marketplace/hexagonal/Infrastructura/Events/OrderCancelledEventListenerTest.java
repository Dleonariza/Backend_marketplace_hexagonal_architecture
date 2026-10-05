package com.marketplace.hexagonal.Infrastructura.Events;

import com.marketplace.hexagonal.Domain.Order.Events.OrderCancelledEvent;
import com.marketplace.hexagonal.Domain.Order.Models.OrderId;
import com.marketplace.hexagonal.Domain.User.Models.UserModel;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import com.marketplace.hexagonal.Domain.User.Ports.Out.UserRepositoryPort;
import com.marketplace.hexagonal.Infrastructura.Adapters.Websocket.OrderStatusBroadcastService;
import com.marketplace.hexagonal.Infrastructura.DTO.Notification.OrderCancelledNotificationDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderCancelledEventListenerTest {

    @Mock UserRepositoryPort userRepo;
    @Mock OrderStatusBroadcastService broadcastService;
    OrderCancelledEventListener listener;

    private static final UserId BUYER_ID = UserId.of("550e8400-e29b-41d4-a716-446655440000");
    private static final String BUYER_EMAIL = "buyer@test.com";
    private static final OrderId ORDER_ID = OrderId.generate();
    private static final String ORDER_NUM = "O0000000001";

    @BeforeEach
    void setUp() {
        listener = new OrderCancelledEventListener(userRepo, broadcastService);
    }

    @Test
    void onOrderCancelled_byBuyer_broadcastsCancellationToBuyer() {
        when(userRepo.findById(BUYER_ID)).thenReturn(buildBuyer());

        listener.onOrderCancelled(buildEvent("BUYER"));

        verify(broadcastService).broadcastToUser(
                eq(BUYER_EMAIL),
                argThat(dto -> {
                    if (!(dto instanceof OrderCancelledNotificationDTO n)) return false;
                    return "ORDER_CANCELLED".equals(n.type())
                            && "BUYER".equals(n.cancelledBy())
                            && ORDER_NUM.equals(n.orderNumber());
                }));
    }

    @Test
    void onOrderCancelled_bySeller_broadcastsCancellationToBuyer() {
        when(userRepo.findById(BUYER_ID)).thenReturn(buildBuyer());

        listener.onOrderCancelled(buildEvent("SELLER"));

        verify(broadcastService).broadcastToUser(
                eq(BUYER_EMAIL),
                argThat(dto -> dto instanceof OrderCancelledNotificationDTO n
                        && "SELLER".equals(n.cancelledBy())));
    }

    @Test
    void onOrderCancelled_byAdmin_broadcastsCancellationToBuyer() {
        when(userRepo.findById(BUYER_ID)).thenReturn(buildBuyer());

        listener.onOrderCancelled(buildEvent("ADMIN"));

        verify(broadcastService).broadcastToUser(
                eq(BUYER_EMAIL),
                argThat(dto -> dto instanceof OrderCancelledNotificationDTO n
                        && "ADMIN".equals(n.cancelledBy())));
    }

    @Test
    void onOrderCancelled_broadcastError_doesNotThrow() {
        when(userRepo.findById(BUYER_ID)).thenReturn(buildBuyer());
        doThrow(new RuntimeException("WS unavailable"))
                .when(broadcastService).broadcastToUser(any(), any());

        listener.onOrderCancelled(buildEvent("BUYER"));

        verify(broadcastService).broadcastToUser(any(), any());
    }

    private OrderCancelledEvent buildEvent(String cancelledBy) {
        return new OrderCancelledEvent(ORDER_ID, ORDER_NUM, BUYER_ID, cancelledBy, Instant.now());
    }

    private UserModel buildBuyer() {
        return UserModel.create(BUYER_EMAIL, "buyer1", "hash", "Buyer Name",
                null, "3001234567", List.of());
    }
}
