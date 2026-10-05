package com.marketplace.hexagonal.Infrastructura.DTO.Chat;

import com.marketplace.hexagonal.Domain.Chat.Models.ChatMessage;

import java.time.Instant;

public record ChatMessageDTO(
        String messageId,
        String roomId,
        String senderId,
        String senderName,
        String content,
        Instant sentAt,
        boolean read
) {
    public static ChatMessageDTO from(ChatMessage m) {
        return new ChatMessageDTO(
                m.getId().getValue(),
                m.getRoomId().getValue(),
                m.getSenderId().getValue(),
                m.getSenderName(),
                m.getContent(),
                m.getSentAt(),
                m.isRead());
    }
}
