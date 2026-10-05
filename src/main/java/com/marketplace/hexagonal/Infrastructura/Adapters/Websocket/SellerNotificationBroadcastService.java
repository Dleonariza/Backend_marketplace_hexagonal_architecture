package com.marketplace.hexagonal.Infrastructura.Adapters.Websocket;

import com.marketplace.hexagonal.Infrastructura.DTO.Notification.LowStockAlertDTO;
import com.marketplace.hexagonal.Infrastructura.DTO.Notification.SellerVerificationNotificationDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SellerNotificationBroadcastService {

    public static final String SELLER_QUEUE = "/queue/seller-notifications";

    private final SimpMessagingTemplate messagingTemplate;

    public void broadcastVerification(String sellerEmail, SellerVerificationNotificationDTO dto) {
        log.debug("Pushing verification notification to seller '{}': status={}",
                sellerEmail, dto.status());
        messagingTemplate.convertAndSendToUser(sellerEmail, SELLER_QUEUE, dto);
    }

    public void broadcastLowStock(String sellerEmail, LowStockAlertDTO dto) {
        log.debug("Pushing low-stock alert to seller '{}': product={}, sku={}, stock={}",
                sellerEmail, dto.productName(), dto.sku(), dto.currentStock());
        messagingTemplate.convertAndSendToUser(sellerEmail, SELLER_QUEUE, dto);
    }
}
