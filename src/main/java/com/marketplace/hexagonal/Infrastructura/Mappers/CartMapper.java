package com.marketplace.hexagonal.Infrastructura.Mappers;

import com.marketplace.hexagonal.Domain.Cart.Models.*;
import com.marketplace.hexagonal.Domain.Coupon.Models.AppliedCoupon;
import com.marketplace.hexagonal.Domain.Coupon.Models.DiscountType;
import com.marketplace.hexagonal.Domain.Product.Models.Money;
import com.marketplace.hexagonal.Domain.Product.Models.ProductId;
import com.marketplace.hexagonal.Domain.Product.Models.ProductVariantId;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import com.marketplace.hexagonal.Infrastructura.Entities.Cart.CartEntity;
import com.marketplace.hexagonal.Infrastructura.Entities.Cart.CartItemEntity;
import com.marketplace.hexagonal.Infrastructura.Entities.Cart.StockReservationEntity;
import com.marketplace.hexagonal.Infrastructura.Entities.Product.ProductEntity;
import com.marketplace.hexagonal.Infrastructura.Entities.Product.ProductVariantEntity;
import com.marketplace.hexagonal.Infrastructura.Entities.Seller.SellerProfileEntity;
import com.marketplace.hexagonal.Infrastructura.Entities.User.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CartMapper {

    default CartItemEntity toItemEntity(CartItem item, CartEntity cartEntity,
                                        ProductEntity productEntity,
                                        ProductVariantEntity variantEntity,
                                        SellerProfileEntity sellerEntity) {
        if (item == null) return null;

        return CartItemEntity.builder()
                .id(UUID.fromString(item.getId().getValue()))
                .cart(cartEntity)
                .product(productEntity)
                .variant(variantEntity)
                .seller(sellerEntity)
                .quantity(item.getQuantity())
                .unitPriceAmount(item.getUnitPrice().amount())
                .unitPriceCurrency(item.getUnitPrice().currency())
                .productName(item.getProductName())
                .productSlug(item.getProductSlug())
                .primaryImageUrl(item.getPrimaryImageUrl())
                .variantSku(item.getVariantSku())
                .variantAttributes(item.getVariantAttributes())
                .addedAt(item.getAddedAt())
                .build();
    }

    default CartItem toItemDomain(CartItemEntity entity) {
        if (entity == null) return null;

        return CartItem.builder()
                .id(CartItemId.of(entity.getId().toString()))
                .productId(ProductId.of(entity.getProduct().getId().toString()))
                .variantId(entity.getVariant() != null
                        ? ProductVariantId.of(entity.getVariant().getId().toString()) : null)
                .sellerId(SellerProfileId.of(entity.getSeller().getId().toString()))
                .quantity(entity.getQuantity())
                .unitPrice(Money.of(entity.getUnitPriceAmount(), entity.getUnitPriceCurrency()))
                .productName(entity.getProductName())
                .productSlug(entity.getProductSlug())
                .primaryImageUrl(entity.getPrimaryImageUrl())
                .variantSku(entity.getVariantSku())
                .variantAttributes(entity.getVariantAttributes())
                .addedAt(entity.getAddedAt())
                .build();
    }

    default Cart toDomain(CartEntity entity) {
        if (entity == null) return null;

        List<CartItem> items = entity.getItems() != null
                ? entity.getItems().stream().map(this::toItemDomain).collect(Collectors.toList())
                : new ArrayList<>();

        UserId userId = entity.getUser() != null
                ? UserId.of(entity.getUser().getId().toString()) : null;

        AppliedCoupon appliedCoupon = toAppliedCoupon(entity);

        return Cart.reconstitute(
                CartId.of(entity.getId().toString()),
                userId,
                entity.getSessionId(),
                items,
                entity.getStatus(),
                appliedCoupon,
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getExpiresAt());
    }

    default AppliedCoupon toAppliedCoupon(CartEntity entity) {
        if (entity.getCouponCode() == null) return null;
        return new AppliedCoupon(
                entity.getCouponCode(),
                entity.getCouponDiscountType(),
                entity.getCouponDiscountValue(),
                Money.of(entity.getCouponDiscountAmount(), entity.getCouponDiscountCurrency()));
    }

    default StockReservation toReservationDomain(StockReservationEntity entity) {
        if (entity == null) return null;

        return StockReservation.reconstitute(
                StockReservationId.of(entity.getId().toString()),
                CartId.of(entity.getCartId().toString()),
                ProductId.of(entity.getProduct().getId().toString()),
                entity.getVariant() != null
                        ? ProductVariantId.of(entity.getVariant().getId().toString()) : null,
                entity.getQuantity(),
                entity.getStatus(),
                entity.getExpiresAt(),
                entity.getCreatedAt());
    }
}
