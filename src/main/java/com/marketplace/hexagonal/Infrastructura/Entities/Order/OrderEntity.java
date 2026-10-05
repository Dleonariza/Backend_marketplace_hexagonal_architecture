package com.marketplace.hexagonal.Infrastructura.Entities.Order;

import com.marketplace.hexagonal.Domain.Order.Models.OrderStatus;
import com.marketplace.hexagonal.Infrastructura.Entities.User.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
    name = "orders",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_order_user_idempotency",
        columnNames = {"user_id", "idempotency_key"}
    )
)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class OrderEntity {

    @Id
    @Column(columnDefinition = "UUID DEFAULT uuid_generate_v4()")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "num_order", nullable = false, unique = true, length = 26, updatable = false)
    private String numOrder;

    @Column(name = "idempotency_key", nullable = false, length = 64)
    private String idempotencyKey;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL,
               orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<SubOrderEntity> subOrders = new ArrayList<>();

    @Column(name = "subtotal_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal subtotalAmount;

    @Column(name = "subtotal_currency", nullable = false, length = 3)
    private String subtotalCurrency;

    @Column(name = "discount_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal discountAmount;

    @Column(name = "discount_currency", nullable = false, length = 3)
    private String discountCurrency;

    @Column(name = "shipping_cost_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal shippingCostAmount;

    @Column(name = "shipping_cost_currency", nullable = false, length = 3)
    private String shippingCostCurrency;

    @Column(name = "total_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal totalAmount;

    @Column(name = "total_currency", nullable = false, length = 3)
    private String totalCurrency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status;

    @Column(name = "shipping_address_id", length = 36)
    private String shippingAddressId;

    @Column(name = "coupon_code", length = 50)
    private String couponCode;

    @Column(name = "payment_method_id", length = 36)
    private String paymentMethodId;

    @Column(name = "payment_last4", length = 4)
    private String paymentLast4;

    @Column(name = "payment_brand", length = 20)
    private String paymentBrand;

    @Column(name = "payment_exp_month")
    private Integer paymentExpMonth;

    @Column(name = "payment_exp_year")
    private Integer paymentExpYear;

    @Column(name = "payment_cardholder_name")
    private String paymentCardholderName;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
