package com.marketplace.hexagonal.Infrastructura.Events;

import com.marketplace.hexagonal.Domain.Chat.Events.ChatMessageSentEvent;
import com.marketplace.hexagonal.Domain.Chat.Models.ChatRoomType;
import com.marketplace.hexagonal.Domain.User.Models.UserModel;
import com.marketplace.hexagonal.Domain.User.Ports.Out.UserRepositoryPort;
import com.marketplace.hexagonal.Infrastructura.Adapters.Websocket.ChatBroadcastService;
import com.marketplace.hexagonal.Infrastructura.DTO.Chat.ChatMessageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatMessageEventListener {

    private final ChatBroadcastService broadcastService;
    private final UserRepositoryPort userRepo;

    @EventListener
    public void onChatMessageSent(ChatMessageSentEvent event) {
        ChatMessageDTO dto = ChatMessageDTO.from(event.message());
        String roomId = event.roomId().getValue();

        try {
            broadcastService.broadcastToRoom(roomId, dto);

            notifyOtherParticipant(event, dto);
        } catch (Exception ex) {
            log.error("Failed to broadcast chat message (roomId={}, messageId={}): {}",
                    roomId, dto.messageId(), ex.getMessage(), ex);
        }
    }

    private void notifyOtherParticipant(ChatMessageSentEvent event, ChatMessageDTO dto) {
        String senderId = event.message().getSenderId().getValue();

        if (event.roomType() == ChatRoomType.BUYER_SELLER) {
            boolean senderIsBuyer = event.buyerId().getValue().equals(senderId);
            if (senderIsBuyer && event.sellerUserId() != null) {
                findEmailAndNotify(event.sellerUserId().getValue(), dto);
            } else {
                findEmailAndNotify(event.buyerId().getValue(), dto);
            }
        } else {
            boolean senderIsBuyer = event.buyerId().getValue().equals(senderId);
            if (!senderIsBuyer) {
                findEmailAndNotify(event.buyerId().getValue(), dto);
            }
        }
    }

    private void findEmailAndNotify(String userId, ChatMessageDTO dto) {
        try {
            com.marketplace.hexagonal.Domain.User.Models.UserModel user =
                    userRepo.findById(com.marketplace.hexagonal.Domain.User.Models.UserId.of(userId));
            broadcastService.notifyUser(user.getEmail(), dto);
        } catch (Exception ex) {
            log.warn("Cannot notify user '{}': {}", userId, ex.getMessage());
        }
    }
}
