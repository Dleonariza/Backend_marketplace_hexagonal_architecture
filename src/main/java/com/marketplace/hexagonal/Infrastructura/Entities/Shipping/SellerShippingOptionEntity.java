package com.marketplace.hexagonal.Infrastructura.Entities.Shipping;

import com.marketplace.hexagonal.Infrastructura.Entities.Seller.SellerProfileEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "seller_shipping_options")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class SellerShippingOptionEntity {

    @Id
    @Column(columnDefinition = "UUID DEFAULT uuid_generate_v4()")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seller_id", nullable = false)
    private SellerProfileEntity seller;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(name = "price_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal priceAmount;

    @Column(name = "price_currency", nullable = false, length = 3)
    private String priceCurrency;

    @Column(name = "estimated_days_min", nullable = false)
    private int estimatedDaysMin;

    @Column(name = "estimated_days_max", nullable = false)
    private int estimatedDaysMax;

    @Column(nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void prePersist() {
        if (id == null) id = UUID.randomUUID();
        if (createdAt == null) createdAt = Instant.now();
        if (!active && createdAt != null) active = true;
    }
}
