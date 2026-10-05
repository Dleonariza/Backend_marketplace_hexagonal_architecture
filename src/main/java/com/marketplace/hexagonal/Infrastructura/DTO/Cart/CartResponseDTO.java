package com.marketplace.hexagonal.Infrastructura.DTO.Cart;

import com.marketplace.hexagonal.Domain.Cart.Models.Cart;
import com.marketplace.hexagonal.Domain.Cart.Models.CartItemId;
import com.marketplace.hexagonal.Domain.Cart.Models.PriceChangeInfo;
import com.marketplace.hexagonal.Infrastructura.DTO.Coupon.AppliedCouponDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record CartResponseDTO(
        String cartId,
        String status,
        List<CartItemResponseDTO> items,
        BigDecimal subtotal,
        BigDecimal discount,
        BigDecimal total,
        String currency,
        int itemCount,
        boolean hasPriceChanges,
        AppliedCouponDTO appliedCoupon
) {
    public static CartResponseDTO from(Cart cart, List<PriceChangeInfo> priceChanges) {
        Map<CartItemId, PriceChangeInfo> changeMap = priceChanges.stream()
                .collect(Collectors.toMap(PriceChangeInfo::cartItemId, pc -> pc));

        List<CartItemResponseDTO> items = cart.getItems().stream()
                .map(item -> CartItemResponseDTO.from(item, changeMap.get(item.getId())))
                .collect(Collectors.toList());

        boolean hasPriceChanges = priceChanges.stream().anyMatch(PriceChangeInfo::changed);

        BigDecimal subtotal = cart.getTotal().amount();
        BigDecimal discount = cart.getDiscount().amount();
        BigDecimal total = cart.getFinalTotal().amount();
        String currency = cart.getTotal().currency();

        return new CartResponseDTO(
                cart.getId().getValue(),
                cart.getStatus().name(),
                items,
                subtotal,
                discount,
                total,
                currency,
                cart.getItems().size(),
                hasPriceChanges,
                AppliedCouponDTO.from(cart.getAppliedCoupon()));
    }

    public static CartResponseDTO from(Cart cart) {
        return from(cart, List.of());
    }
}
