package com.marketplace.hexagonal.Domain.Chat.Ports.Out;

import com.marketplace.hexagonal.Domain.Chat.Models.ChatRoom;
import com.marketplace.hexagonal.Domain.Chat.Models.ChatRoomId;
import com.marketplace.hexagonal.Domain.Chat.Models.ChatRoomType;
import com.marketplace.hexagonal.Domain.User.Models.UserId;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepositoryPort {

    ChatRoom save(ChatRoom room);

    Optional<ChatRoom> findById(ChatRoomId id);

    Optional<ChatRoom> findByOrderIdAndTypeAndSellerUserId(String orderId,
                                                            ChatRoomType type,
                                                            UserId sellerUserId);

    List<ChatRoom> findByParticipant(UserId userId);

    List<ChatRoom> findAllBuyerSupportRooms();

    void updateLastActivity(ChatRoomId id, java.time.Instant lastActivityAt);

    int deleteInactiveSupportRooms(java.time.Instant cutoff);
}
