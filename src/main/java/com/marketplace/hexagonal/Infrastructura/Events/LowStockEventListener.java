package com.marketplace.hexagonal.Infrastructura.Events;

import com.marketplace.hexagonal.Domain.Product.Events.LowStockAlertEvent;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfile;
import com.marketplace.hexagonal.Domain.Seller.Ports.Out.SellerProfileRepositoryPort;
import com.marketplace.hexagonal.Domain.User.Models.UserModel;
import com.marketplace.hexagonal.Domain.User.Ports.Out.UserRepositoryPort;
import com.marketplace.hexagonal.Infrastructura.Adapters.Websocket.SellerNotificationBroadcastService;
import com.marketplace.hexagonal.Infrastructura.DTO.Notification.LowStockAlertDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class LowStockEventListener {

    private final SellerProfileRepositoryPort sellerProfileRepo;
    private final UserRepositoryPort userRepo;
    private final SellerNotificationBroadcastService broadcastService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onLowStock(LowStockAlertEvent event) {
        log.debug("Low stock alert: product={}, sku={}, stock={}",
                event.productId().getValue(), event.sku(), event.currentStock());
        try {
            SellerProfile seller = sellerProfileRepo.findById(event.sellerProfileId())
                    .orElseThrow(() -> new IllegalStateException(
                            "Seller profile not found: " + event.sellerProfileId().getValue()));

            UserModel sellerUser = userRepo.findById(seller.getUserId());
            LowStockAlertDTO dto = LowStockAlertDTO.from(event);
            broadcastService.broadcastLowStock(sellerUser.getEmail(), dto);
            log.debug("Low stock alert sent to seller '{}'", sellerUser.getEmail());
        } catch (Exception ex) {
            log.error("Failed to broadcast low-stock alert (product={}, sku={}): {}",
                    event.productId().getValue(), event.sku(), ex.getMessage(), ex);
        }
    }
}
