package com.marketplace.hexagonal.Application.Chat.Services;

import com.marketplace.hexagonal.Domain.Chat.Models.ChatMessage;
import com.marketplace.hexagonal.Domain.Chat.Models.ChatRoom;
import com.marketplace.hexagonal.Domain.Chat.Models.ChatRoomId;
import com.marketplace.hexagonal.Domain.Chat.Models.ChatRoomType;
import com.marketplace.hexagonal.Domain.Chat.Ports.In.*;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ChatService {

    private final CreateOrGetChatRoomUseCase createOrGetChatRoom;
    private final SendChatMessageUseCase sendChatMessage;
    private final GetChatMessagesUseCase getChatMessages;
    private final GetUserChatRoomsUseCase getUserChatRooms;

    public ChatRoom getOrCreateBuyerSellerRoom(UserId requesterId,
                                               boolean isAdmin,
                                               String orderId,
                                               String sellerProfileId) {
        return createOrGetChatRoom.execute(
                requesterId, isAdmin, orderId, ChatRoomType.BUYER_SELLER, sellerProfileId);
    }

    public ChatRoom getOrCreateSupportRoom(UserId requesterId,
                                           boolean isAdmin,
                                           String orderId) {
        return createOrGetChatRoom.execute(
                requesterId, isAdmin, orderId, ChatRoomType.BUYER_SUPPORT, null);
    }

    public ChatMessage sendMessage(ChatRoomId roomId,
                                   UserId senderId,
                                   String senderName,
                                   boolean senderIsAdmin,
                                   String content) {
        return sendChatMessage.execute(roomId, senderId, senderName, senderIsAdmin, content);
    }

    public List<ChatMessage> getMessages(ChatRoomId roomId,
                                         UserId requesterId,
                                         boolean isAdmin,
                                         int page,
                                         int size) {
        return getChatMessages.execute(roomId, requesterId, isAdmin, page, size);
    }

    public List<ChatRoom> getUserRooms(UserId userId, boolean isAdmin) {
        return getUserChatRooms.execute(userId, isAdmin);
    }

    public long countMessages(ChatRoomId roomId, UserId requesterId, boolean isAdmin) {
        return getChatMessages.count(roomId, requesterId, isAdmin);
    }
}
