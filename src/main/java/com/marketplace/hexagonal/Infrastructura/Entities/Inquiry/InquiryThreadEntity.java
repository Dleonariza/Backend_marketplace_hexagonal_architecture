package com.marketplace.hexagonal.Infrastructura.Entities.Inquiry;

import com.marketplace.hexagonal.Domain.Inquiry.Models.InquiryStatus;
import com.marketplace.hexagonal.Infrastructura.Entities.User.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
    name = "inquiry_threads",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_inquiry_thread_product_buyer",
        columnNames = {"product_id", "buyer_id"}
    )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InquiryThreadEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    private UserEntity buyer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_user_id", nullable = false)
    private UserEntity sellerUser;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private InquiryStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "last_activity_at", nullable = false)
    private Instant lastActivityAt;

    @OneToMany(mappedBy = "thread", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InquiryMessageEntity> messages;
}
