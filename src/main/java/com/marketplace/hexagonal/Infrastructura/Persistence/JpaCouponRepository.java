package com.marketplace.hexagonal.Infrastructura.Persistence;

import com.marketplace.hexagonal.Infrastructura.Entities.Coupon.CouponEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JpaCouponRepository extends JpaRepository<CouponEntity, UUID> {
    Optional<CouponEntity> findByCode(String code);
}
