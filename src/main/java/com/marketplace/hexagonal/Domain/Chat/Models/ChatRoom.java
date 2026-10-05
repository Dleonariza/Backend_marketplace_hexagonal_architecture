package com.marketplace.hexagonal.Domain.Chat.Models;

import com.marketplace.hexagonal.Domain.User.Models.UserId;
import lombok.*;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class ChatRoom {

    private ChatRoomId id;

    private String orderId;
    private ChatRoomType type;

    private UserId buyerId;

    private UserId sellerUserId;

    private Instant createdAt;

    private Instant lastActivityAt;

    public static ChatRoom create(String orderId,
                                  ChatRoomType type,
                                  UserId buyerId,
                                  UserId sellerUserId) {
        Instant now = Instant.now();
        return ChatRoom.builder()
                .id(ChatRoomId.generate())
                .orderId(orderId)
                .type(type)
                .buyerId(buyerId)
                .sellerUserId(sellerUserId)
                .createdAt(now)
                .lastActivityAt(now)
                .build();
    }

    public static ChatRoom reconstitute(ChatRoomId id,
                                        String orderId,
                                        ChatRoomType type,
                                        UserId buyerId,
                                        UserId sellerUserId,
                                        Instant createdAt,
                                        Instant lastActivityAt) {
        ChatRoom r = new ChatRoom();
        r.id = id;
        r.orderId = orderId;
        r.type = type;
        r.buyerId = buyerId;
        r.sellerUserId = sellerUserId;
        r.createdAt = createdAt;
        r.lastActivityAt = lastActivityAt;
        return r;
    }

    public void touch() {
        this.lastActivityAt = Instant.now();
    }

    public boolean isParticipant(UserId userId, boolean isAdmin) {
        if (buyerId.equals(userId)) return true;
        if (type == ChatRoomType.BUYER_SUPPORT && isAdmin) return true;
        if (type == ChatRoomType.BUYER_SELLER
                && sellerUserId != null
                && sellerUserId.equals(userId)) return true;
        return false;
    }
}
