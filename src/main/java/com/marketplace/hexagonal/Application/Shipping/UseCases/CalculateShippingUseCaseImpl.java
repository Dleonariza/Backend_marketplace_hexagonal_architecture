package com.marketplace.hexagonal.Application.Shipping.UseCases;

import com.marketplace.hexagonal.Domain.Cart.Models.Cart;
import com.marketplace.hexagonal.Domain.Cart.Models.CartItem;
import com.marketplace.hexagonal.Domain.Cart.Ports.Out.CartRepositoryPort;
import com.marketplace.hexagonal.Domain.Product.Models.Money;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;
import com.marketplace.hexagonal.Domain.Seller.Ports.Out.SellerProfileRepositoryPort;
import com.marketplace.hexagonal.Domain.Shipping.Models.SellerShippingOption;
import com.marketplace.hexagonal.Domain.Shipping.Models.ShippingQuote;
import com.marketplace.hexagonal.Domain.Shipping.Ports.In.CalculateShippingUseCase;
import com.marketplace.hexagonal.Domain.Shipping.Ports.Out.SellerShippingOptionRepositoryPort;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class CalculateShippingUseCaseImpl implements CalculateShippingUseCase {

    private final CartRepositoryPort cartRepo;
    private final SellerShippingOptionRepositoryPort shippingRepo;
    private final SellerProfileRepositoryPort sellerRepo;

    @Override
    public List<ShippingQuote> calculateShipping(UserId userId, String sessionId) {
        Cart cart = findCart(userId, sessionId);
        if (cart == null || cart.getItems().isEmpty()) return List.of();

        Map<SellerProfileId, List<CartItem>> bySeller = new LinkedHashMap<>();
        for (CartItem item : cart.getItems()) {
            bySeller.computeIfAbsent(item.getSellerId(), k -> new ArrayList<>()).add(item);
        }

        List<ShippingQuote> quotes = new ArrayList<>();
        for (Map.Entry<SellerProfileId, List<CartItem>> entry : bySeller.entrySet()) {
            SellerProfileId sellerId = entry.getKey();
            List<CartItem> items = entry.getValue();

            String storeName = sellerRepo.findById(sellerId)
                    .map(p -> p.getStoreName())
                    .orElse("Unknown Store");

            Money subtotal = computeSubtotal(items);

            List<SellerShippingOption> options =
                    shippingRepo.findActivesBySellerId(sellerId);

            quotes.add(new ShippingQuote(sellerId.getValue(), storeName, subtotal, options));
        }

        return quotes;
    }

    private Cart findCart(UserId userId, String sessionId) {
        if (userId != null)
            return cartRepo.findActiveByUserId(userId).orElse(null);
        if (sessionId != null)
            return cartRepo.findActiveBySessionId(sessionId).orElse(null);
        return null;
    }

    private Money computeSubtotal(List<CartItem> items) {
        String currency = items.get(0).getUnitPrice().currency();
        BigDecimal total = items.stream()
                .map(i -> i.getUnitPrice().amount()
                            .multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return Money.of(total, currency);
    }
}
