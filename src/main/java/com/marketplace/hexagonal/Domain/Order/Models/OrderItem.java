package com.marketplace.hexagonal.Domain.Order.Models;

import com.marketplace.hexagonal.Domain.Cart.Models.CartItem;
import com.marketplace.hexagonal.Domain.Product.Models.Money;
import com.marketplace.hexagonal.Domain.Product.Models.ProductId;
import com.marketplace.hexagonal.Domain.Product.Models.ProductVariantId;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;
import lombok.*;

import java.math.BigDecimal;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class OrderItem {

    private OrderItemId id;
    private ProductId productId;

    private ProductVariantId variantId;
    private SellerProfileId sellerId;

    private String productName;
    private String productSlug;
    private String primaryImageUrl;

    private String variantSku;
    private Map<String, String> variantAttributes;

    private int quantity;
    private Money unitPrice;

    private Money itemSubtotal;

    public static OrderItem fromCartItem(CartItem cartItem) {
        Money subtotal = Money.of(
                cartItem.getUnitPrice().amount()
                        .multiply(BigDecimal.valueOf(cartItem.getQuantity())),
                cartItem.getUnitPrice().currency());

        return OrderItem.builder()
                .id(OrderItemId.generate())
                .productId(cartItem.getProductId())
                .variantId(cartItem.getVariantId())
                .sellerId(cartItem.getSellerId())
                .productName(cartItem.getProductName())
                .productSlug(cartItem.getProductSlug())
                .primaryImageUrl(cartItem.getPrimaryImageUrl())
                .variantSku(cartItem.getVariantSku())
                .variantAttributes(cartItem.getVariantAttributes())
                .quantity(cartItem.getQuantity())
                .unitPrice(cartItem.getUnitPrice())
                .itemSubtotal(subtotal)
                .build();
    }
}
