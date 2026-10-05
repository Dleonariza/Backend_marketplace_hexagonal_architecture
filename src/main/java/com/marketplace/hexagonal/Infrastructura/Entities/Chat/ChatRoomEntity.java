package com.marketplace.hexagonal.Infrastructura.Entities.Chat;

import com.marketplace.hexagonal.Domain.Chat.Models.ChatRoomType;
import com.marketplace.hexagonal.Infrastructura.Entities.User.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
    name = "chat_rooms",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_chat_room_order_type_seller",
        columnNames = {"order_id", "type", "seller_user_id"}
    )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ChatRoomType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    private UserEntity buyer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_user_id")
    private UserEntity sellerUser;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "last_activity_at", nullable = false)
    private Instant lastActivityAt;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatMessageEntity> messages;
}
