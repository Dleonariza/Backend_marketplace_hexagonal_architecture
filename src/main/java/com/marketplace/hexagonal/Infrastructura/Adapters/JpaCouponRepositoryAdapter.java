package com.marketplace.hexagonal.Infrastructura.Adapters;

import com.marketplace.hexagonal.Domain.Coupon.Exceptions.CouponNotFoundException;
import com.marketplace.hexagonal.Domain.Coupon.Models.Coupon;
import com.marketplace.hexagonal.Domain.Coupon.Models.CouponId;
import com.marketplace.hexagonal.Domain.Coupon.Ports.Out.CouponRepositoryPort;
import com.marketplace.hexagonal.Infrastructura.Entities.Coupon.CouponEntity;
import com.marketplace.hexagonal.Infrastructura.Mappers.CouponMapper;
import com.marketplace.hexagonal.Infrastructura.Persistence.JpaCouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JpaCouponRepositoryAdapter implements CouponRepositoryPort {

    private final JpaCouponRepository jpaRepo;
    private final CouponMapper mapper;

    @Override
    public Coupon save(Coupon coupon) {
        CouponEntity entity = mapper.toEntity(coupon);
        return mapper.toDomain(jpaRepo.save(entity));
    }

    @Override
    public Optional<Coupon> findByCode(String code) {
        return jpaRepo.findByCode(code).map(mapper::toDomain);
    }

    @Override
    public Optional<Coupon> findById(CouponId id) {
        return jpaRepo.findById(UUID.fromString(id.getValue())).map(mapper::toDomain);
    }

    @Override
    public List<Coupon> findAll() {
        return jpaRepo.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
