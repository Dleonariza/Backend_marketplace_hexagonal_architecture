package com.marketplace.hexagonal.Application.Chat.UseCases;

import com.marketplace.hexagonal.Domain.Chat.Models.ChatRoom;
import com.marketplace.hexagonal.Domain.Chat.Ports.In.GetUserChatRoomsUseCase;
import com.marketplace.hexagonal.Domain.Chat.Ports.Out.ChatRoomRepositoryPort;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class GetUserChatRoomsUseCaseImpl implements GetUserChatRoomsUseCase {

    private final ChatRoomRepositoryPort chatRoomRepo;

    @Override
    public List<ChatRoom> execute(UserId userId, boolean isAdmin) {
        if (isAdmin) return chatRoomRepo.findAllBuyerSupportRooms();
        return chatRoomRepo.findByParticipant(userId);
    }
}
