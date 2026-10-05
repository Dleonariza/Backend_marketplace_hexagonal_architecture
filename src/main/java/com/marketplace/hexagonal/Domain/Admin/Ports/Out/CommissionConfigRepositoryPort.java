package com.marketplace.hexagonal.Domain.Admin.Ports.Out;

import com.marketplace.hexagonal.Domain.Admin.Models.CommissionConfig;

import java.util.Optional;

public interface CommissionConfigRepositoryPort {
    CommissionConfig save(CommissionConfig config);
    Optional<CommissionConfig> findLatest();
}
