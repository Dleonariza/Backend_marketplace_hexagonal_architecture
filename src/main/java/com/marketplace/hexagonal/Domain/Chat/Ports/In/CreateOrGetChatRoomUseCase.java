package com.marketplace.hexagonal.Domain.Chat.Ports.In;

import com.marketplace.hexagonal.Domain.Chat.Models.ChatRoom;
import com.marketplace.hexagonal.Domain.Chat.Models.ChatRoomType;
import com.marketplace.hexagonal.Domain.User.Models.UserId;

public interface CreateOrGetChatRoomUseCase {

    ChatRoom execute(UserId requesterId,
                     boolean isAdmin,
                     String orderId,
                     ChatRoomType type,
                     String sellerProfileId);
}
