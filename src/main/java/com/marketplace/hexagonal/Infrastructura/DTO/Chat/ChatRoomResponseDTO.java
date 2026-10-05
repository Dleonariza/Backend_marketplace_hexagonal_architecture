package com.marketplace.hexagonal.Infrastructura.DTO.Chat;

import com.marketplace.hexagonal.Domain.Chat.Models.ChatRoom;
import com.marketplace.hexagonal.Domain.Chat.Models.ChatRoomType;

import java.time.Instant;

public record ChatRoomResponseDTO(
        String roomId,
        String orderId,
        ChatRoomType type,
        String buyerId,
        String sellerUserId,
        Instant createdAt
) {
    public static ChatRoomResponseDTO from(ChatRoom room) {
        return new ChatRoomResponseDTO(
                room.getId().getValue(),
                room.getOrderId(),
                room.getType(),
                room.getBuyerId().getValue(),
                room.getSellerUserId() != null ? room.getSellerUserId().getValue() : null,
                room.getCreatedAt());
    }
}
