package com.marketplace.hexagonal.Domain.Chat.Ports.Out;

import com.marketplace.hexagonal.Domain.Chat.Models.ChatMessage;
import com.marketplace.hexagonal.Domain.Chat.Models.ChatRoomId;

import java.util.List;

public interface ChatMessageRepositoryPort {

    ChatMessage save(ChatMessage message);

    List<ChatMessage> findByRoomId(ChatRoomId roomId, int page, int size);

    long countByRoomId(ChatRoomId roomId);
}
