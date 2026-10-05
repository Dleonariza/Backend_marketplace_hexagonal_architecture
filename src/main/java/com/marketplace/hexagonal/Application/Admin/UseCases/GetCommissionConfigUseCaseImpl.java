package com.marketplace.hexagonal.Application.Admin.UseCases;

import com.marketplace.hexagonal.Domain.Admin.Models.CommissionConfig;
import com.marketplace.hexagonal.Domain.Admin.Ports.In.GetCommissionConfigUseCase;
import com.marketplace.hexagonal.Domain.Admin.Ports.Out.CommissionConfigRepositoryPort;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class GetCommissionConfigUseCaseImpl implements GetCommissionConfigUseCase {

    private final CommissionConfigRepositoryPort commissionRepo;

    @Override
    public CommissionConfig getConfig() {
        return commissionRepo.findLatest()
                .orElseGet(() -> CommissionConfig.create(
                        new BigDecimal("5.00"), "Default marketplace commission"));
    }
}
