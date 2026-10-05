package com.marketplace.hexagonal.Domain.Chat.Ports.In;

import com.marketplace.hexagonal.Domain.Chat.Models.ChatMessage;
import com.marketplace.hexagonal.Domain.Chat.Models.ChatRoomId;
import com.marketplace.hexagonal.Domain.User.Models.UserId;

import java.util.List;

public interface GetChatMessagesUseCase {

    List<ChatMessage> execute(ChatRoomId roomId,
                              UserId requesterId,
                              boolean isAdmin,
                              int page,
                              int size);

    long count(ChatRoomId roomId, UserId requesterId, boolean isAdmin);
}
