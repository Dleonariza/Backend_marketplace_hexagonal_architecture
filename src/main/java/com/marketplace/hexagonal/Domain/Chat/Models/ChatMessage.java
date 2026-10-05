package com.marketplace.hexagonal.Domain.Chat.Models;

import com.marketplace.hexagonal.Domain.User.Models.UserId;
import lombok.*;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class ChatMessage {

    private ChatMessageId id;
    private ChatRoomId roomId;
    private UserId senderId;

    private String senderName;
    private String content;
    private Instant sentAt;

    private boolean read;

    public static ChatMessage create(ChatRoomId roomId,
                                     UserId senderId,
                                     String senderName,
                                     String content) {
        if (content == null || content.isBlank())
            throw new IllegalArgumentException("Chat message content must not be blank.");
        return ChatMessage.builder()
                .id(ChatMessageId.generate())
                .roomId(roomId)
                .senderId(senderId)
                .senderName(senderName)
                .content(content.strip())
                .sentAt(Instant.now())
                .read(false)
                .build();
    }

    public static ChatMessage reconstitute(ChatMessageId id,
                                           ChatRoomId roomId,
                                           UserId senderId,
                                           String senderName,
                                           String content,
                                           Instant sentAt,
                                           boolean read) {
        ChatMessage m = new ChatMessage();
        m.id = id;
        m.roomId = roomId;
        m.senderId = senderId;
        m.senderName = senderName;
        m.content = content;
        m.sentAt = sentAt;
        m.read = read;
        return m;
    }
}
