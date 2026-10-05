package com.marketplace.hexagonal.Infrastructura.Adapters.Websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderStatusBroadcastService {

    public static final String USER_QUEUE = "/queue/order-status";

    public static final String ADMIN_TOPIC = "/topic/admin/order-updates";

    private final SimpMessagingTemplate messagingTemplate;

    public void broadcastToUser(String userEmail, Object payload) {
        log.debug("Pushing order notification to user '{}'", userEmail);
        messagingTemplate.convertAndSendToUser(userEmail, USER_QUEUE, payload);
    }

    public void broadcastToAdmin(Object payload) {
        log.debug("Broadcasting order update to admin topic");
        messagingTemplate.convertAndSend(ADMIN_TOPIC, payload);
    }
}
