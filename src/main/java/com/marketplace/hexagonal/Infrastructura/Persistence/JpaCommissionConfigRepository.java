package com.marketplace.hexagonal.Infrastructura.Persistence;

import com.marketplace.hexagonal.Infrastructura.Entities.Admin.CommissionConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaCommissionConfigRepository extends JpaRepository<CommissionConfigEntity, Long> {

    Optional<CommissionConfigEntity> findTopByOrderByIdDesc();
}
