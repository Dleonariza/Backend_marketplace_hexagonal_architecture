package com.marketplace.hexagonal.Infrastructura.Mappers;

import com.marketplace.hexagonal.Domain.Coupon.Models.Coupon;
import com.marketplace.hexagonal.Domain.Coupon.Models.CouponId;
import com.marketplace.hexagonal.Infrastructura.Entities.Coupon.CouponEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.UUID;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CouponMapper {

    default CouponEntity toEntity(Coupon coupon) {
        if (coupon == null) return null;
        return CouponEntity.builder()
                .id(UUID.fromString(coupon.getId().getValue()))
                .code(coupon.getCode())
                .description(coupon.getDescription())
                .discountType(coupon.getDiscountType())
                .discountValue(coupon.getDiscountValue())
                .minimumOrderAmount(coupon.getMinimumOrderAmount())
                .maxUses(coupon.getMaxUses())
                .usedCount(coupon.getUsedCount())
                .validFrom(coupon.getValidFrom())
                .validUntil(coupon.getValidUntil())
                .active(coupon.isActive())
                .createdAt(coupon.getCreatedAt())
                .build();
    }

    default Coupon toDomain(CouponEntity entity) {
        if (entity == null) return null;
        return Coupon.reconstitute(
                CouponId.of(entity.getId().toString()),
                entity.getCode(),
                entity.getDescription(),
                entity.getDiscountType(),
                entity.getDiscountValue(),
                entity.getMinimumOrderAmount(),
                entity.getMaxUses(),
                entity.getUsedCount(),
                entity.getValidFrom(),
                entity.getValidUntil(),
                entity.isActive(),
                entity.getCreatedAt());
    }
}
