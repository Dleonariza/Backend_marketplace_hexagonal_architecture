package com.marketplace.hexagonal.Infrastructura.Entities.User;

import com.marketplace.hexagonal.Domain.User.Models.UserStatus;
import com.marketplace.hexagonal.Infrastructura.Entities.Chat.ChatMessageEntity;
import com.marketplace.hexagonal.Infrastructura.Entities.Chat.ChatRoomEntity;
import com.marketplace.hexagonal.Infrastructura.Entities.Favorite.FavoriteEntity;
import com.marketplace.hexagonal.Infrastructura.Entities.Review.ReviewEntity;
import com.marketplace.hexagonal.Infrastructura.Entities.Seller.SellerProfileEntity;
import com.marketplace.hexagonal.Infrastructura.Entities.User.RoleAndPermission.RoleEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "UUID DEFAULT uuid_generate_v4()")
    private UUID id;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(unique = true, nullable = false)
    private String userName;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String name;
    @Column
    private String avatarUrl;
    @Column(nullable = false)
    @Size(min = 10)
    private String phone;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserStatus status;
    @Column(name = "create_at")
    private Instant createdAt;
    @Column(name = "updated_at")
    private Instant updatedAt;
    @Column
    private boolean enabled;
    @Column(name = "account_No_Expired")
    private boolean accountNoExpired;
    @Column(name = "account_No_Locked")
    private boolean accountNoLocked;
    @Column(name = "credential_No_Expired")
    private boolean credentialNoExpired;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<RoleEntity> roles;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserAddressEntity> userAddressEntity;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PaymentMethodEntity> paymentMethods;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FavoriteEntity> favorites;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewEntity> reviews;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private SellerProfileEntity sellerProfile;

    @OneToMany(mappedBy = "buyer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatRoomEntity> chatRoomsAsBuyer;

    @OneToMany(mappedBy = "sellerUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatRoomEntity> chatRoomsAsSeller;

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatMessageEntity> sentMessages;

    @PrePersist
    void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }
}
