package com.marketplace.hexagonal.Infrastructura.Mappers;

import com.marketplace.hexagonal.Domain.Order.Models.*;
import com.marketplace.hexagonal.Domain.Order.Models.PaymentInfo;
import com.marketplace.hexagonal.Domain.Product.Models.Money;
import com.marketplace.hexagonal.Domain.Product.Models.ProductId;
import com.marketplace.hexagonal.Domain.Product.Models.ProductVariantId;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import com.marketplace.hexagonal.Infrastructura.Entities.Order.OrderEntity;
import com.marketplace.hexagonal.Infrastructura.Entities.Order.OrderItemEntity;
import com.marketplace.hexagonal.Infrastructura.Entities.Order.SubOrderEntity;
import com.marketplace.hexagonal.Infrastructura.Entities.User.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderMapper {

    default OrderEntity toEntity(Order order, UserEntity user) {
        if (order == null) return null;

        OrderEntity entity = OrderEntity.builder()
                .id(UUID.fromString(order.getId().getValue()))
                .user(user)
                .numOrder(order.getNumOrder())
                .idempotencyKey(order.getIdempotencyKey())
                .status(order.getStatus())
                .subtotalAmount(order.getSubtotal().amount())
                .subtotalCurrency(order.getSubtotal().currency())
                .discountAmount(order.getDiscount().amount())
                .discountCurrency(order.getDiscount().currency())
                .shippingCostAmount(order.getShippingCost().amount())
                .shippingCostCurrency(order.getShippingCost().currency())
                .totalAmount(order.getTotal().amount())
                .totalCurrency(order.getTotal().currency())
                .shippingAddressId(order.getShippingAddressId())
                .couponCode(order.getCouponCode())
                .paymentMethodId(order.getPaymentInfo() != null
                        ? order.getPaymentInfo().paymentMethodId() : null)
                .paymentLast4(order.getPaymentInfo() != null
                        ? order.getPaymentInfo().last4() : null)
                .paymentBrand(order.getPaymentInfo() != null
                        ? order.getPaymentInfo().brand() : null)
                .paymentExpMonth(order.getPaymentInfo() != null
                        ? order.getPaymentInfo().expMonth() : null)
                .paymentExpYear(order.getPaymentInfo() != null
                        ? order.getPaymentInfo().expYear() : null)
                .paymentCardholderName(order.getPaymentInfo() != null
                        ? order.getPaymentInfo().cardholderName() : null)
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();

        List<SubOrderEntity> subOrderEntities = order.getSubOrders().stream()
                .map(s -> toSubOrderEntity(s, entity))
                .collect(java.util.stream.Collectors.toCollection(ArrayList::new));
        entity.setSubOrders(subOrderEntities);
        return entity;
    }

    default SubOrderEntity toSubOrderEntity(SubOrder subOrder, OrderEntity orderEntity) {
        SubOrderEntity entity = SubOrderEntity.builder()
                .id(UUID.fromString(subOrder.getId().getValue()))
                .order(orderEntity)
                .sellerId(UUID.fromString(subOrder.getSellerId().getValue()))
                .storeName(subOrder.getStoreName())
                .subtotalAmount(subOrder.getSubtotal().amount())
                .subtotalCurrency(subOrder.getSubtotal().currency())
                .shippingCostAmount(subOrder.getShippingCost().amount())
                .shippingCostCurrency(subOrder.getShippingCost().currency())
                .totalAmount(subOrder.getTotal().amount())
                .totalCurrency(subOrder.getTotal().currency())
                .shippingOptionId(subOrder.getShippingOptionId())
                .status(subOrder.getStatus())
                .createdAt(subOrder.getCreatedAt())
                .updatedAt(subOrder.getUpdatedAt())
                .build();

        List<OrderItemEntity> itemEntities = subOrder.getItems().stream()
                .map(item -> toItemEntity(item, entity))
                .toList();
        entity.getItems().addAll(itemEntities);
        return entity;
    }

    default OrderItemEntity toItemEntity(OrderItem item, SubOrderEntity subOrderEntity) {
        return OrderItemEntity.builder()
                .id(UUID.fromString(item.getId().getValue()))
                .subOrder(subOrderEntity)
                .productId(UUID.fromString(item.getProductId().getValue()))
                .variantId(item.getVariantId() != null
                        ? UUID.fromString(item.getVariantId().getValue()) : null)
                .sellerId(UUID.fromString(item.getSellerId().getValue()))
                .productName(item.getProductName())
                .productSlug(item.getProductSlug())
                .primaryImageUrl(item.getPrimaryImageUrl())
                .variantSku(item.getVariantSku())
                .variantAttributes(item.getVariantAttributes())
                .quantity(item.getQuantity())
                .unitPriceAmount(item.getUnitPrice().amount())
                .unitPriceCurrency(item.getUnitPrice().currency())
                .itemSubtotalAmount(item.getItemSubtotal().amount())
                .itemSubtotalCurrency(item.getItemSubtotal().currency())
                .build();
    }

    default Order toDomain(OrderEntity entity, List<SubOrder> subOrders) {
        if (entity == null) return null;

        PaymentInfo paymentInfo = entity.getPaymentLast4() != null
                ? new PaymentInfo(
                        entity.getPaymentMethodId(),
                        entity.getPaymentLast4(),
                        entity.getPaymentBrand(),
                        entity.getPaymentExpMonth() != null ? entity.getPaymentExpMonth() : 0,
                        entity.getPaymentExpYear() != null ? entity.getPaymentExpYear() : 0,
                        entity.getPaymentCardholderName())
                : null;

        return Order.reconstitute(
                OrderId.of(entity.getId().toString()),
                UserId.of(entity.getUser().getId().toString()),
                entity.getNumOrder(),
                entity.getIdempotencyKey(),
                subOrders,
                Money.of(entity.getSubtotalAmount(), entity.getSubtotalCurrency()),
                Money.of(entity.getDiscountAmount(), entity.getDiscountCurrency()),
                Money.of(entity.getShippingCostAmount(), entity.getShippingCostCurrency()),
                Money.of(entity.getTotalAmount(), entity.getTotalCurrency()),
                entity.getStatus(),
                entity.getShippingAddressId(),
                entity.getCouponCode(),
                paymentInfo,
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    default SubOrder toSubOrderDomain(SubOrderEntity entity) {
        List<OrderItem> items = entity.getItems().stream()
                .map(this::toItemDomain)
                .toList();

        return SubOrder.reconstitute(
                SubOrderId.of(entity.getId().toString()),
                OrderId.of(entity.getOrder().getId().toString()),
                SellerProfileId.of(entity.getSellerId().toString()),
                entity.getStoreName(),
                items,
                Money.of(entity.getSubtotalAmount(), entity.getSubtotalCurrency()),
                Money.of(entity.getShippingCostAmount(), entity.getShippingCostCurrency()),
                Money.of(entity.getTotalAmount(), entity.getTotalCurrency()),
                entity.getShippingOptionId(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    default OrderItem toItemDomain(OrderItemEntity entity) {
        return OrderItem.builder()
                .id(OrderItemId.of(entity.getId().toString()))
                .productId(ProductId.of(entity.getProductId().toString()))
                .variantId(entity.getVariantId() != null
                        ? ProductVariantId.of(entity.getVariantId().toString()) : null)
                .sellerId(SellerProfileId.of(entity.getSellerId().toString()))
                .productName(entity.getProductName())
                .productSlug(entity.getProductSlug())
                .primaryImageUrl(entity.getPrimaryImageUrl())
                .variantSku(entity.getVariantSku())
                .variantAttributes(entity.getVariantAttributes())
                .quantity(entity.getQuantity())
                .unitPrice(Money.of(entity.getUnitPriceAmount(), entity.getUnitPriceCurrency()))
                .itemSubtotal(Money.of(entity.getItemSubtotalAmount(), entity.getItemSubtotalCurrency()))
                .build();
    }
}
