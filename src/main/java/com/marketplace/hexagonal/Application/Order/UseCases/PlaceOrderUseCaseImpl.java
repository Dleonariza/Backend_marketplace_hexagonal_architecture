package com.marketplace.hexagonal.Application.Order.UseCases;

import com.marketplace.hexagonal.Domain.Cart.Exceptions.CartNotFoundException;
import com.marketplace.hexagonal.Domain.Cart.Models.Cart;
import com.marketplace.hexagonal.Domain.Cart.Models.CartItem;
import com.marketplace.hexagonal.Domain.Cart.Ports.Out.CartRepositoryPort;
import com.marketplace.hexagonal.Domain.Order.Exceptions.EmptyCartException;
import com.marketplace.hexagonal.Domain.Order.Models.*;
import com.marketplace.hexagonal.Domain.Order.Ports.In.PlaceOrderUseCase;
import com.marketplace.hexagonal.Domain.Order.Ports.Out.OrderNumberGeneratorPort;
import com.marketplace.hexagonal.Domain.Order.Ports.Out.OrderRepositoryPort;
import com.marketplace.hexagonal.Domain.Order.Ports.Out.SubOrderRepositoryPort;
import com.marketplace.hexagonal.Domain.Product.Models.Money;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfile;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;
import com.marketplace.hexagonal.Domain.Seller.Ports.Out.SellerProfileRepositoryPort;
import com.marketplace.hexagonal.Domain.Shipping.Models.SellerShippingOption;
import com.marketplace.hexagonal.Domain.Shipping.Models.ShippingOptionId;
import com.marketplace.hexagonal.Domain.Shipping.Ports.Out.SellerShippingOptionRepositoryPort;
import com.marketplace.hexagonal.Domain.User.Exceptions.PaymentMethodNotFoundException;
import com.marketplace.hexagonal.Domain.User.Models.PaymentMethod;
import com.marketplace.hexagonal.Domain.User.Models.PaymentMethodId;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import com.marketplace.hexagonal.Domain.User.Ports.Out.PaymentMethodRepositoryPort;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.*;

@RequiredArgsConstructor
public class PlaceOrderUseCaseImpl implements PlaceOrderUseCase {

    private final OrderRepositoryPort orderRepo;
    private final SubOrderRepositoryPort subOrderRepo;
    private final CartRepositoryPort cartRepo;
    private final SellerShippingOptionRepositoryPort shippingRepo;
    private final SellerProfileRepositoryPort sellerProfileRepo;
    private final OrderNumberGeneratorPort orderNumberGenerator;
    private final PaymentMethodRepositoryPort paymentMethodRepo;

    @Override
    public Order execute(UserId userId,
                         String idempotencyKey,
                         String shippingAddressId,
                         Map<String, String> shippingSelections,
                         String paymentMethodId) {
        Optional<Order> existing =
                orderRepo.findByUserIdAndIdempotencyKey(userId, idempotencyKey);
        if (existing.isPresent()) return existing.get();

        Cart cart = cartRepo.findActiveByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException(
                        "No active cart found for user: " + userId.getValue()));

        if (cart.isEmpty())
            throw new EmptyCartException("Cannot place an order with an empty cart.");

        String currency = cart.getTotal().currency();

        Map<SellerProfileId, List<CartItem>> bySeller = groupBySeller(cart.getItems());

        OrderId orderId = OrderId.generate();

        List<SubOrder> subOrders = new ArrayList<>();
        for (Map.Entry<SellerProfileId, List<CartItem>> entry : bySeller.entrySet()) {
            SellerProfileId sellerId = entry.getKey();
            List<CartItem> sellerItems = entry.getValue();

            String optionId = shippingSelections != null
                    ? shippingSelections.get(sellerId.getValue()) : null;
            Money shippingCost = resolveShippingCost(optionId, currency);
            String storeName = resolveStoreName(sellerId);

            SubOrder subOrder = SubOrder.create(
                    orderId, sellerId, storeName,
                    sellerItems, shippingCost, optionId);
            subOrders.add(subOrder);
        }

        Money discount = cart.getDiscount();
        String couponCode = cart.getAppliedCoupon() != null
                ? cart.getAppliedCoupon().couponCode() : null;

        String numOrder = orderNumberGenerator.next();

        PaymentInfo paymentInfo = resolvePaymentInfo(userId, paymentMethodId);

        Order order = Order.place(orderId, userId, numOrder, idempotencyKey,
                subOrders, discount, shippingAddressId, couponCode, paymentInfo);

        orderRepo.save(order);

        cart.checkout();
        cartRepo.save(cart);

        return order;
    }

    private Map<SellerProfileId, List<CartItem>> groupBySeller(List<CartItem> items) {
        Map<SellerProfileId, List<CartItem>> map = new LinkedHashMap<>();
        for (CartItem item : items) {
            map.computeIfAbsent(item.getSellerId(), k -> new ArrayList<>()).add(item);
        }
        return map;
    }

    private Money resolveShippingCost(String optionId, String currency) {
        if (optionId == null || optionId.isBlank())
            return Money.of(BigDecimal.ZERO, currency);
        return shippingRepo.findById(ShippingOptionId.of(optionId))
                .map(SellerShippingOption::getPrice)
                .orElse(Money.of(BigDecimal.ZERO, currency));
    }

    private String resolveStoreName(SellerProfileId sellerId) {
        return sellerProfileRepo.findById(sellerId)
                .map(SellerProfile::getStoreName)
                .orElse("Unknown Store");
    }

    private PaymentInfo resolvePaymentInfo(UserId userId, String paymentMethodId) {
        if (paymentMethodId == null || paymentMethodId.isBlank()) return null;

        PaymentMethod pm = paymentMethodRepo
                .findById(PaymentMethodId.of(paymentMethodId))
                .filter(p -> p.getUserId().equals(userId))
                .orElseThrow(() -> new PaymentMethodNotFoundException(
                        "Payment method not found or does not belong to this user: " + paymentMethodId));

        return new PaymentInfo(
                pm.getId().getValue(),
                pm.getLast4(),
                pm.getBrand(),
                pm.getExpMonth(),
                pm.getExpYear(),
                pm.getCardholderName()
        );
    }
}
