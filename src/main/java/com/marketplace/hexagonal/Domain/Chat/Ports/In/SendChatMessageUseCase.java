package com.marketplace.hexagonal.Domain.Chat.Ports.In;

import com.marketplace.hexagonal.Domain.Chat.Models.ChatMessage;
import com.marketplace.hexagonal.Domain.Chat.Models.ChatRoomId;
import com.marketplace.hexagonal.Domain.User.Models.UserId;

public interface SendChatMessageUseCase {

    ChatMessage execute(ChatRoomId roomId,
                        UserId senderId,
                        String senderName,
                        boolean senderIsAdmin,
                        String content);
}
