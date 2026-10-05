package com.marketplace.hexagonal.Infrastructura.Events;

import com.marketplace.hexagonal.Domain.Order.Events.SubOrderStatusChangedEvent;
import com.marketplace.hexagonal.Domain.Order.Models.Order;
import com.marketplace.hexagonal.Domain.Order.Ports.Out.OrderRepositoryPort;
import com.marketplace.hexagonal.Domain.User.Models.UserModel;
import com.marketplace.hexagonal.Domain.User.Ports.Out.UserRepositoryPort;
import com.marketplace.hexagonal.Infrastructura.Adapters.Websocket.OrderStatusBroadcastService;
import com.marketplace.hexagonal.Infrastructura.DTO.Order.OrderStatusUpdateDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderStatusEventListener {

    private final OrderRepositoryPort orderRepo;
    private final UserRepositoryPort userRepo;
    private final OrderStatusBroadcastService broadcastService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onSubOrderStatusChanged(SubOrderStatusChangedEvent event) {
        log.debug("Sub-order {} changed to {}, preparing broadcast...",
                event.subOrderId().getValue(), event.newStatus());
        try {
            OrderStatusUpdateDTO dto = OrderStatusUpdateDTO.from(event);

            broadcastService.broadcastToAdmin(dto);

            orderRepo.findById(event.orderId()).ifPresent(order -> {
                UserModel buyer = userRepo.findById(order.getUserId());
                broadcastService.broadcastToUser(buyer.getEmail(), dto);
                log.debug("Order status notification sent to buyer '{}'", buyer.getEmail());
            });
        } catch (Exception ex) {
            log.error("Failed to broadcast sub-order status update (subOrderId={}, newStatus={}): {}",
                    event.subOrderId().getValue(), event.newStatus(), ex.getMessage(), ex);
        }
    }
}
