package com.marketplace.hexagonal.Infrastructura.Events;

import com.marketplace.hexagonal.Application.Admin.Services.AdminService;
import com.marketplace.hexagonal.Domain.Order.Events.OrderPlacedEvent;
import com.marketplace.hexagonal.Infrastructura.Adapters.Websocket.DashboardBroadcastService;
import com.marketplace.hexagonal.Infrastructura.DTO.Admin.AdminDashboardResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class DashboardEventListener {

    private final AdminService adminService;
    private final DashboardBroadcastService broadcastService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onOrderPlaced(OrderPlacedEvent event) {
        log.debug("Order placed (id={}), broadcasting dashboard update...", event.orderId());
        try {
            AdminDashboardResponseDTO stats =
                    AdminDashboardResponseDTO.from(adminService.getStats());
            broadcastService.broadcastStats(stats);
        } catch (Exception ex) {
            log.error("Failed to broadcast dashboard stats after order placed (orderId={}): {}",
                    event.orderId(), ex.getMessage(), ex);
        }
    }
}
