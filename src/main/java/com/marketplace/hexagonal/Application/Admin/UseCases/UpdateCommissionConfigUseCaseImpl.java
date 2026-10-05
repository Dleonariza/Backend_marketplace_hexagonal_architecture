package com.marketplace.hexagonal.Application.Admin.UseCases;

import com.marketplace.hexagonal.Domain.Admin.Models.CommissionConfig;
import com.marketplace.hexagonal.Domain.Admin.Ports.In.UpdateCommissionConfigUseCase;
import com.marketplace.hexagonal.Domain.Admin.Ports.Out.CommissionConfigRepositoryPort;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class UpdateCommissionConfigUseCaseImpl implements UpdateCommissionConfigUseCase {

    private final CommissionConfigRepositoryPort commissionRepo;

    @Override
    public CommissionConfig update(BigDecimal rate, String description) {
        if (rate == null || rate.compareTo(BigDecimal.ZERO) < 0
                || rate.compareTo(new BigDecimal("100")) > 0)
            throw new IllegalArgumentException("Commission rate must be between 0 and 100.");

        return commissionRepo.save(CommissionConfig.create(rate, description));
    }
}
