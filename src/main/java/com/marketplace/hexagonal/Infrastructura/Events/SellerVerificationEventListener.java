package com.marketplace.hexagonal.Infrastructura.Events;

import com.marketplace.hexagonal.Domain.Seller.Events.SellerVerificationCompletedEvent;
import com.marketplace.hexagonal.Domain.User.Models.UserModel;
import com.marketplace.hexagonal.Domain.User.Ports.Out.UserRepositoryPort;
import com.marketplace.hexagonal.Infrastructura.Adapters.Websocket.SellerNotificationBroadcastService;
import com.marketplace.hexagonal.Infrastructura.DTO.Notification.SellerVerificationNotificationDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class SellerVerificationEventListener {

    private final UserRepositoryPort userRepo;
    private final SellerNotificationBroadcastService broadcastService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onSellerVerificationCompleted(SellerVerificationCompletedEvent event) {
        log.debug("Seller verification completed for userId={}, status={}",
                event.sellerUserId().getValue(), event.newStatus());
        try {
            UserModel seller = userRepo.findById(event.sellerUserId());
            SellerVerificationNotificationDTO dto =
                    SellerVerificationNotificationDTO.from(event);
            broadcastService.broadcastVerification(seller.getEmail(), dto);
            log.debug("Verification notification sent to seller '{}'", seller.getEmail());
        } catch (Exception ex) {
            log.error("Failed to broadcast seller verification notification (userId={}, status={}): {}",
                    event.sellerUserId().getValue(), event.newStatus(), ex.getMessage(), ex);
        }
    }
}
