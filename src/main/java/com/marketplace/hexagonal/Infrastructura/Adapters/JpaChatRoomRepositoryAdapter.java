package com.marketplace.hexagonal.Infrastructura.Adapters;

import com.marketplace.hexagonal.Domain.Chat.Models.ChatRoom;
import com.marketplace.hexagonal.Domain.Chat.Models.ChatRoomId;
import com.marketplace.hexagonal.Domain.Chat.Models.ChatRoomType;
import com.marketplace.hexagonal.Domain.Chat.Ports.Out.ChatRoomRepositoryPort;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import com.marketplace.hexagonal.Infrastructura.Entities.Chat.ChatRoomEntity;
import com.marketplace.hexagonal.Infrastructura.Entities.User.UserEntity;
import com.marketplace.hexagonal.Infrastructura.Persistence.JpaChatRoomRepository;
import com.marketplace.hexagonal.Infrastructura.Persistence.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JpaChatRoomRepositoryAdapter implements ChatRoomRepositoryPort {

    private final JpaChatRoomRepository jpaChatRoomRepository;
    private final JpaUserRepository jpaUserRepository;

    @Override
    @Transactional
    public ChatRoom save(ChatRoom room) {
        UserEntity buyer = findUser(room.getBuyerId());
        UserEntity seller = room.getSellerUserId() != null
                ? findUser(room.getSellerUserId())
                : null;

        ChatRoomEntity entity = ChatRoomEntity.builder()
                .orderId(UUID.fromString(room.getOrderId()))
                .type(room.getType())
                .buyer(buyer)
                .sellerUser(seller)
                .createdAt(room.getCreatedAt())
                .lastActivityAt(room.getLastActivityAt())
                .build();

        return toDomain(jpaChatRoomRepository.save(entity));
    }

    @Override
    public Optional<ChatRoom> findById(ChatRoomId id) {
        return jpaChatRoomRepository
                .findById(UUID.fromString(id.getValue()))
                .map(this::toDomain);
    }

    @Override
    public Optional<ChatRoom> findByOrderIdAndTypeAndSellerUserId(String orderId,
                                                                   ChatRoomType type,
                                                                   UserId sellerUserId) {
        UUID sellerUuid = sellerUserId != null
                ? UUID.fromString(sellerUserId.getValue())
                : null;
        return jpaChatRoomRepository
                .findByOrderIdAndTypeAndSellerUserId(
                        UUID.fromString(orderId), type, sellerUuid)
                .map(this::toDomain);
    }

    @Override
    public List<ChatRoom> findByParticipant(UserId userId) {
        UUID uid = UUID.fromString(userId.getValue());
        List<ChatRoomEntity> rooms = new ArrayList<>();
        rooms.addAll(jpaChatRoomRepository.findByBuyer_Id(uid));
        rooms.addAll(jpaChatRoomRepository.findBySellerUser_Id(uid));
        return rooms.stream().map(this::toDomain).toList();
    }

    @Override
    @Transactional
    public void updateLastActivity(ChatRoomId id, Instant lastActivityAt) {
        jpaChatRoomRepository.updateLastActivity(
                UUID.fromString(id.getValue()), lastActivityAt);
    }

    @Override
    @Transactional
    public int deleteInactiveSupportRooms(Instant cutoff) {
        return jpaChatRoomRepository.deleteByTypeAndLastActivityAtBefore(cutoff);
    }

    @Override
    public List<ChatRoom> findAllBuyerSupportRooms() {
        return jpaChatRoomRepository
                .findByType(ChatRoomType.BUYER_SUPPORT)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private ChatRoom toDomain(ChatRoomEntity e) {
        return ChatRoom.reconstitute(
                ChatRoomId.of(e.getId().toString()),
                e.getOrderId().toString(),
                e.getType(),
                UserId.of(e.getBuyer().getId().toString()),
                e.getSellerUser() != null
                        ? UserId.of(e.getSellerUser().getId().toString())
                        : null,
                e.getCreatedAt(),
                e.getLastActivityAt());
    }

    private UserEntity findUser(UserId userId) {
        return jpaUserRepository
                .findById(UUID.fromString(userId.getValue()))
                .orElseThrow(() -> new IllegalArgumentException(
                        "User not found: " + userId.getValue()));
    }
}
