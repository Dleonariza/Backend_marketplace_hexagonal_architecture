package com.marketplace.hexagonal.Infrastructura.Persistence;

import com.marketplace.hexagonal.Infrastructura.Entities.User.PaymentMethodEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaPaymentMethodRepository extends JpaRepository<PaymentMethodEntity, UUID> {

    List<PaymentMethodEntity> findAllByUserId(UUID userId);

    boolean existsByIdAndUserId(UUID id, UUID userId);

    @Modifying
    @Query("DELETE FROM PaymentMethodEntity p WHERE p.id = :id")
    void deletePaymentMethodById(UUID id);

    @Modifying
    @Query("UPDATE PaymentMethodEntity p SET p.isDefault = false WHERE p.user.id = :userId")
    void clearDefaultByUserId(UUID userId);
}
