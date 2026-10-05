package com.marketplace.hexagonal.Infrastructura.Entities.Order;

import com.marketplace.hexagonal.Domain.Order.Models.SubOrderStatus;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "sub_orders")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class SubOrderEntity {

    @Id
    @Column(columnDefinition = "UUID DEFAULT uuid_generate_v4()")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;

    @Column(name = "seller_id", nullable = false)
    private UUID sellerId;

    @Column(name = "store_name", nullable = false, length = 200)
    private String storeName;

    @OneToMany(mappedBy = "subOrder", cascade = CascadeType.ALL,
               orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<OrderItemEntity> items = new ArrayList<>();

    @Column(name = "subtotal_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal subtotalAmount;

    @Column(name = "subtotal_currency", nullable = false, length = 3)
    private String subtotalCurrency;

    @Column(name = "shipping_cost_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal shippingCostAmount;

    @Column(name = "shipping_cost_currency", nullable = false, length = 3)
    private String shippingCostCurrency;

    @Column(name = "total_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal totalAmount;

    @Column(name = "total_currency", nullable = false, length = 3)
    private String totalCurrency;

    @Column(name = "shipping_option_id", length = 36)
    private String shippingOptionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SubOrderStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
