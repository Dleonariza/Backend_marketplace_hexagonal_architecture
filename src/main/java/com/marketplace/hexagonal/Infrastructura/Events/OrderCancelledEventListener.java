package com.marketplace.hexagonal.Infrastructura.Events;

import com.marketplace.hexagonal.Domain.Order.Events.OrderCancelledEvent;
import com.marketplace.hexagonal.Domain.User.Models.UserModel;
import com.marketplace.hexagonal.Domain.User.Ports.Out.UserRepositoryPort;
import com.marketplace.hexagonal.Infrastructura.Adapters.Websocket.OrderStatusBroadcastService;
import com.marketplace.hexagonal.Infrastructura.DTO.Notification.OrderCancelledNotificationDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCancelledEventListener {

    private final UserRepositoryPort userRepo;
    private final OrderStatusBroadcastService broadcastService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onOrderCancelled(OrderCancelledEvent event) {
        log.debug("Order cancelled: orderId={}, cancelledBy={}",
                event.orderId().getValue(), event.cancelledBy());
        try {
            UserModel buyer = userRepo.findById(event.buyerUserId());
            OrderCancelledNotificationDTO dto = OrderCancelledNotificationDTO.from(event);
            broadcastService.broadcastToUser(buyer.getEmail(), dto);
            log.debug("Cancellation notification sent to buyer '{}'", buyer.getEmail());
        } catch (Exception ex) {
            log.error("Failed to broadcast order cancellation (orderId={}, cancelledBy={}): {}",
                    event.orderId().getValue(), event.cancelledBy(), ex.getMessage(), ex);
        }
    }
}
