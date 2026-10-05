package com.marketplace.hexagonal.Infrastructura.Adapters.Websocket;

import com.marketplace.hexagonal.Infrastructura.DTO.Admin.AdminDashboardResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardBroadcastService {

    public static final String TOPIC = "/topic/admin/dashboard";

    private final SimpMessagingTemplate messagingTemplate;

    public void broadcastStats(AdminDashboardResponseDTO stats) {
        log.debug("Broadcasting dashboard update to {}", TOPIC);
        messagingTemplate.convertAndSend(TOPIC, stats);
    }
}
