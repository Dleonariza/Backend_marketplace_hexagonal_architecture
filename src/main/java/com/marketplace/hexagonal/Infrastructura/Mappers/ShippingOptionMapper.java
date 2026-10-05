package com.marketplace.hexagonal.Infrastructura.Mappers;

import com.marketplace.hexagonal.Domain.Product.Models.Money;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;
import com.marketplace.hexagonal.Domain.Shipping.Models.SellerShippingOption;
import com.marketplace.hexagonal.Domain.Shipping.Models.ShippingOptionId;
import com.marketplace.hexagonal.Infrastructura.Entities.Seller.SellerProfileEntity;
import com.marketplace.hexagonal.Infrastructura.Entities.Shipping.SellerShippingOptionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.UUID;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ShippingOptionMapper {

    default SellerShippingOptionEntity toEntity(SellerShippingOption option,
                                                 SellerProfileEntity sellerEntity) {
        if (option == null) return null;
        return SellerShippingOptionEntity.builder()
                .id(UUID.fromString(option.getId().getValue()))
                .seller(sellerEntity)
                .name(option.getName())
                .description(option.getDescription())
                .priceAmount(option.getPrice().amount())
                .priceCurrency(option.getPrice().currency())
                .estimatedDaysMin(option.getEstimatedDaysMin())
                .estimatedDaysMax(option.getEstimatedDaysMax())
                .active(option.isActive())
                .createdAt(option.getCreatedAt())
                .build();
    }

    default SellerShippingOption toDomain(SellerShippingOptionEntity entity) {
        if (entity == null) return null;
        return SellerShippingOption.reconstitute(
                ShippingOptionId.of(entity.getId().toString()),
                SellerProfileId.of(entity.getSeller().getId().toString()),
                entity.getName(),
                entity.getDescription(),
                Money.of(entity.getPriceAmount(), entity.getPriceCurrency()),
                entity.getEstimatedDaysMin(),
                entity.getEstimatedDaysMax(),
                entity.isActive(),
                entity.getCreatedAt());
    }
}
