package com.marketplace.hexagonal.Infrastructura.Entities.Order;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "order_items")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class OrderItemEntity {

    @Id
    @Column(columnDefinition = "UUID DEFAULT uuid_generate_v4()")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_order_id", nullable = false)
    private SubOrderEntity subOrder;

    @Column(name = "product_id", nullable = false)
    private UUID productId;

    @Column(name = "variant_id")
    private UUID variantId;

    @Column(name = "seller_id", nullable = false)
    private UUID sellerId;

    @Column(name = "product_name", nullable = false, length = 200)
    private String productName;

    @Column(name = "product_slug", nullable = false, length = 220)
    private String productSlug;

    @Column(name = "primary_image_url")
    private String primaryImageUrl;

    @Column(name = "variant_sku", length = 100)
    private String variantSku;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "variant_attributes", columnDefinition = "jsonb")
    private Map<String, String> variantAttributes;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "unit_price_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal unitPriceAmount;

    @Column(name = "unit_price_currency", nullable = false, length = 3)
    private String unitPriceCurrency;

    @Column(name = "item_subtotal_amount", nullable = false, precision = 19, scale = 4)
    private BigDecimal itemSubtotalAmount;

    @Column(name = "item_subtotal_currency", nullable = false, length = 3)
    private String itemSubtotalCurrency;
}
