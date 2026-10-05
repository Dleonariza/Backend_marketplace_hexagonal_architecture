package com.marketplace.hexagonal.Application.Chat.UseCases;

import com.marketplace.hexagonal.Domain.Chat.Exceptions.ChatRoomAccessDeniedException;
import com.marketplace.hexagonal.Domain.Chat.Exceptions.ChatRoomNotFoundException;
import com.marketplace.hexagonal.Domain.Chat.Models.ChatMessage;
import com.marketplace.hexagonal.Domain.Chat.Models.ChatRoom;
import com.marketplace.hexagonal.Domain.Chat.Models.ChatRoomId;
import com.marketplace.hexagonal.Domain.Chat.Ports.In.GetChatMessagesUseCase;
import com.marketplace.hexagonal.Domain.Chat.Ports.Out.ChatMessageRepositoryPort;
import com.marketplace.hexagonal.Domain.Chat.Ports.Out.ChatRoomRepositoryPort;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class GetChatMessagesUseCaseImpl implements GetChatMessagesUseCase {

    private final ChatRoomRepositoryPort chatRoomRepo;
    private final ChatMessageRepositoryPort chatMessageRepo;

    @Override
    public List<ChatMessage> execute(ChatRoomId roomId,
                                     UserId requesterId,
                                     boolean isAdmin,
                                     int page,
                                     int size) {
        ChatRoom room = chatRoomRepo.findById(roomId)
                .orElseThrow(() -> new ChatRoomNotFoundException(roomId.getValue()));

        if (!room.isParticipant(requesterId, isAdmin))
            throw new ChatRoomAccessDeniedException();

        return chatMessageRepo.findByRoomId(roomId, page, size);
    }

    @Override
    public long count(ChatRoomId roomId, UserId requesterId, boolean isAdmin) {
        ChatRoom room = chatRoomRepo.findById(roomId)
                .orElseThrow(() -> new ChatRoomNotFoundException(roomId.getValue()));
        if (!room.isParticipant(requesterId, isAdmin))
            throw new ChatRoomAccessDeniedException();
        return chatMessageRepo.countByRoomId(roomId);
    }
}
