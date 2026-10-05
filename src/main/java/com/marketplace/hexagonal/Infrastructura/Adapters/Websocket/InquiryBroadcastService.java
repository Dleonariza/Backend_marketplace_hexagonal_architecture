package com.marketplace.hexagonal.Infrastructura.Adapters.Websocket;

import com.marketplace.hexagonal.Infrastructura.DTO.Inquiry.InquiryMessageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class InquiryBroadcastService {

    public static final String THREAD_TOPIC = "/topic/inquiry/";
    public static final String NOTIFY_QUEUE = "/queue/inquiry-notify";

    private final SimpMessagingTemplate messagingTemplate;

    public void broadcastToThread(String threadId, InquiryMessageDTO dto) {
        log.debug("Broadcasting inquiry message to thread '{}'", threadId);
        messagingTemplate.convertAndSend(THREAD_TOPIC + threadId, dto);
    }

    public void notifyUser(String userEmail, InquiryMessageDTO dto) {
        log.debug("Notifying user '{}' of new inquiry message in thread '{}'",
                userEmail, dto.threadId());
        messagingTemplate.convertAndSendToUser(userEmail, NOTIFY_QUEUE, dto);
    }
}
