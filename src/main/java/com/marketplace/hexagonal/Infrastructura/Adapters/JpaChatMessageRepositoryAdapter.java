package com.marketplace.hexagonal.Infrastructura.Adapters;

import com.marketplace.hexagonal.Domain.Chat.Models.ChatMessage;
import com.marketplace.hexagonal.Domain.Chat.Models.ChatMessageId;
import com.marketplace.hexagonal.Domain.Chat.Models.ChatRoomId;
import com.marketplace.hexagonal.Domain.Chat.Ports.Out.ChatMessageRepositoryPort;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import com.marketplace.hexagonal.Infrastructura.Entities.Chat.ChatMessageEntity;
import com.marketplace.hexagonal.Infrastructura.Entities.Chat.ChatRoomEntity;
import com.marketplace.hexagonal.Infrastructura.Entities.User.UserEntity;
import com.marketplace.hexagonal.Infrastructura.Persistence.JpaChatMessageRepository;
import com.marketplace.hexagonal.Infrastructura.Persistence.JpaChatRoomRepository;
import com.marketplace.hexagonal.Infrastructura.Persistence.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JpaChatMessageRepositoryAdapter implements ChatMessageRepositoryPort {

    private final JpaChatMessageRepository jpaChatMessageRepository;
    private final JpaChatRoomRepository jpaChatRoomRepository;
    private final JpaUserRepository jpaUserRepository;

    @Override
    @Transactional
    public ChatMessage save(ChatMessage message) {
        ChatRoomEntity room = jpaChatRoomRepository
                .findById(UUID.fromString(message.getRoomId().getValue()))
                .orElseThrow(() -> new IllegalArgumentException(
                        "Chat room not found: " + message.getRoomId().getValue()));

        UserEntity sender = jpaUserRepository
                .findById(UUID.fromString(message.getSenderId().getValue()))
                .orElseThrow(() -> new IllegalArgumentException(
                        "Sender not found: " + message.getSenderId().getValue()));

        ChatMessageEntity entity = ChatMessageEntity.builder()
                .room(room)
                .sender(sender)
                .senderName(message.getSenderName())
                .content(message.getContent())
                .sentAt(message.getSentAt())
                .read(message.isRead())
                .build();

        return toDomain(jpaChatMessageRepository.save(entity));
    }

    @Override
    public List<ChatMessage> findByRoomId(ChatRoomId roomId, int page, int size) {
        return jpaChatMessageRepository
                .findByRoom_IdOrderBySentAtDesc(
                        UUID.fromString(roomId.getValue()),
                        PageRequest.of(page, size))
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public long countByRoomId(ChatRoomId roomId) {
        return jpaChatMessageRepository.countByRoom_Id(UUID.fromString(roomId.getValue()));
    }

    private ChatMessage toDomain(ChatMessageEntity e) {
        return ChatMessage.reconstitute(
                ChatMessageId.of(e.getId().toString()),
                ChatRoomId.of(e.getRoom().getId().toString()),
                UserId.of(e.getSender().getId().toString()),
                e.getSenderName(),
                e.getContent(),
                e.getSentAt(),
                e.isRead());
    }
}
