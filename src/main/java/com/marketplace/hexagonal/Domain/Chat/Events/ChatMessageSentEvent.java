package com.marketplace.hexagonal.Domain.Chat.Events;

import com.marketplace.hexagonal.Domain.Chat.Models.ChatMessage;
import com.marketplace.hexagonal.Domain.Chat.Models.ChatRoomId;
import com.marketplace.hexagonal.Domain.Chat.Models.ChatRoomType;
import com.marketplace.hexagonal.Domain.User.Models.UserId;

public record ChatMessageSentEvent(
        ChatRoomId roomId,
        ChatMessage message,
        ChatRoomType roomType,
        UserId buyerId,
        UserId sellerUserId
) {}
