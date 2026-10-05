package com.marketplace.hexagonal.Infrastructura.Entities.Product;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "product_variants")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ProductVariantEntity {

    @Id
    @Column(columnDefinition = "UUID DEFAULT uuid_generate_v4()")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    @Column(nullable = false, length = 100)
    private String sku;

    @ElementCollection
    @CollectionTable(
            name = "product_variant_attributes",
            joinColumns = @JoinColumn(name = "variant_id")
    )
    @MapKeyColumn(name = "attr_key")
    @Column(name = "attr_value")
    @Builder.Default
    private Map<String, String> attributes = new HashMap<>();

    @Column(name = "price_amount", precision = 19, scale = 4)
    private BigDecimal priceAmount;

    @Column(name = "price_currency", length = 3)
    private String priceCurrency;

    @Column(nullable = false)
    private int stock;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
