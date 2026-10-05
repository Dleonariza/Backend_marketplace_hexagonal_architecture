package com.marketplace.hexagonal.Application.Chat.UseCases;

import com.marketplace.hexagonal.Domain.Chat.Events.ChatMessageSentEvent;
import com.marketplace.hexagonal.Domain.Chat.Exceptions.ChatRoomAccessDeniedException;
import com.marketplace.hexagonal.Domain.Chat.Exceptions.ChatRoomNotFoundException;
import com.marketplace.hexagonal.Domain.Chat.Models.ChatMessage;
import com.marketplace.hexagonal.Domain.Chat.Models.ChatRoom;
import com.marketplace.hexagonal.Domain.Chat.Models.ChatRoomId;
import com.marketplace.hexagonal.Domain.Chat.Ports.In.SendChatMessageUseCase;
import com.marketplace.hexagonal.Domain.Chat.Ports.Out.ChatMessageRepositoryPort;
import com.marketplace.hexagonal.Domain.Chat.Ports.Out.ChatRoomRepositoryPort;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;

@RequiredArgsConstructor
public class SendChatMessageUseCaseImpl implements SendChatMessageUseCase {

    private final ChatRoomRepositoryPort chatRoomRepo;
    private final ChatMessageRepositoryPort chatMessageRepo;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public ChatMessage execute(ChatRoomId roomId,
                               UserId senderId,
                               String senderName,
                               boolean senderIsAdmin,
                               String content) {
        ChatRoom room = chatRoomRepo.findById(roomId)
                .orElseThrow(() -> new ChatRoomNotFoundException(roomId.getValue()));

        if (!room.isParticipant(senderId, senderIsAdmin))
            throw new ChatRoomAccessDeniedException();

        ChatMessage message = ChatMessage.create(roomId, senderId, senderName, content);
        ChatMessage saved = chatMessageRepo.save(message);

        room.touch();
        chatRoomRepo.updateLastActivity(roomId, room.getLastActivityAt());

        eventPublisher.publishEvent(new ChatMessageSentEvent(
                roomId,
                saved,
                room.getType(),
                room.getBuyerId(),
                room.getSellerUserId()
        ));

        return saved;
    }
}
