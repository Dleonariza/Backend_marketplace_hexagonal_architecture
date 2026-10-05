package com.marketplace.hexagonal.Infrastructura.Adapters;

import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;
import com.marketplace.hexagonal.Domain.Shipping.Models.SellerShippingOption;
import com.marketplace.hexagonal.Domain.Shipping.Models.ShippingOptionId;
import com.marketplace.hexagonal.Domain.Shipping.Ports.Out.SellerShippingOptionRepositoryPort;
import com.marketplace.hexagonal.Infrastructura.Entities.Seller.SellerProfileEntity;
import com.marketplace.hexagonal.Infrastructura.Entities.Shipping.SellerShippingOptionEntity;
import com.marketplace.hexagonal.Infrastructura.Mappers.ShippingOptionMapper;
import com.marketplace.hexagonal.Infrastructura.Persistence.JpaSellerProfileRepository;
import com.marketplace.hexagonal.Infrastructura.Persistence.JpaSellerShippingOptionRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class JpaSellerShippingOptionRepositoryAdapter implements SellerShippingOptionRepositoryPort {

    private final JpaSellerShippingOptionRepository jpaRepo;
    private final JpaSellerProfileRepository jpaSellerRepo;
    private final ShippingOptionMapper mapper;

    @Override
    public SellerShippingOption save(SellerShippingOption option) {
        SellerProfileEntity seller = jpaSellerRepo
                .findById(UUID.fromString(option.getSellerId().getValue()))
                .orElseThrow(() -> new IllegalStateException(
                        "Seller not found: " + option.getSellerId().getValue()));
        SellerShippingOptionEntity entity = mapper.toEntity(option, seller);
        return mapper.toDomain(jpaRepo.save(entity));
    }

    @Override
    public Optional<SellerShippingOption> findById(ShippingOptionId id) {
        return jpaRepo.findById(UUID.fromString(id.getValue()))
                .map(mapper::toDomain);
    }

    @Override
    public List<SellerShippingOption> findBySellerId(SellerProfileId sellerId) {
        return jpaRepo.findBySeller_Id(UUID.fromString(sellerId.getValue()))
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<SellerShippingOption> findActivesBySellerId(SellerProfileId sellerId) {
        return jpaRepo.findBySeller_IdAndActiveTrue(UUID.fromString(sellerId.getValue()))
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public void deleteById(ShippingOptionId id) {
        jpaRepo.deleteById(UUID.fromString(id.getValue()));
    }
}
