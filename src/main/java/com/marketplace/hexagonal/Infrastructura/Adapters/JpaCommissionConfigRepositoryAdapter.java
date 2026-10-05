package com.marketplace.hexagonal.Infrastructura.Adapters;

import com.marketplace.hexagonal.Domain.Admin.Models.CommissionConfig;
import com.marketplace.hexagonal.Domain.Admin.Ports.Out.CommissionConfigRepositoryPort;
import com.marketplace.hexagonal.Infrastructura.Entities.Admin.CommissionConfigEntity;
import com.marketplace.hexagonal.Infrastructura.Persistence.JpaCommissionConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JpaCommissionConfigRepositoryAdapter implements CommissionConfigRepositoryPort {

    private final JpaCommissionConfigRepository jpaRepo;

    @Override
    public CommissionConfig save(CommissionConfig config) {
        CommissionConfigEntity entity = CommissionConfigEntity.builder()
                .rate(config.rate())
                .description(config.description())
                .build();
        CommissionConfigEntity saved = jpaRepo.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<CommissionConfig> findLatest() {
        return jpaRepo.findTopByOrderByIdDesc().map(this::toDomain);
    }

    private CommissionConfig toDomain(CommissionConfigEntity e) {
        return new CommissionConfig(e.getId(), e.getRate(),
                e.getDescription(), e.getCreatedAt(), e.getUpdatedAt());
    }
}
