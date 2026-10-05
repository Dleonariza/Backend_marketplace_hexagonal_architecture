package com.marketplace.hexagonal.Infrastructura.Adapters.Websocket;

import com.marketplace.hexagonal.Infrastructura.DTO.Chat.ChatMessageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatBroadcastService {

    public static final String ROOM_TOPIC = "/topic/chat/";
    public static final String NOTIFY_QUEUE = "/queue/chat-notify";

    private final SimpMessagingTemplate messagingTemplate;

    public void broadcastToRoom(String roomId, ChatMessageDTO dto) {
        log.debug("Broadcasting chat message to room '{}'", roomId);
        messagingTemplate.convertAndSend(ROOM_TOPIC + roomId, dto);
    }

    public void notifyUser(String userEmail, ChatMessageDTO dto) {
        log.debug("Notifying user '{}' of new chat message in room '{}'",
                userEmail, dto.roomId());
        messagingTemplate.convertAndSendToUser(userEmail, NOTIFY_QUEUE, dto);
    }
}
