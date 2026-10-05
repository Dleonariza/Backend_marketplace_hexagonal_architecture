package com.marketplace.hexagonal.Domain.Chat.Ports.In;

import com.marketplace.hexagonal.Domain.Chat.Models.ChatRoom;
import com.marketplace.hexagonal.Domain.User.Models.UserId;

import java.util.List;

public interface GetUserChatRoomsUseCase {

    List<ChatRoom> execute(UserId userId, boolean isAdmin);
}
