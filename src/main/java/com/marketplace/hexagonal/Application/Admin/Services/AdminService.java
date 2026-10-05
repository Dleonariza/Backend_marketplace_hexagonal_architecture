package com.marketplace.hexagonal.Application.Admin.Services;

import com.marketplace.hexagonal.Domain.Admin.Models.AdminStats;
import com.marketplace.hexagonal.Domain.Admin.Models.CommissionConfig;
import com.marketplace.hexagonal.Domain.Admin.Ports.In.GetAdminStatsUseCase;
import com.marketplace.hexagonal.Domain.Admin.Ports.In.GetCommissionConfigUseCase;
import com.marketplace.hexagonal.Domain.Admin.Ports.In.UpdateCommissionConfigUseCase;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
public class AdminService implements GetAdminStatsUseCase,
        GetCommissionConfigUseCase, UpdateCommissionConfigUseCase {
    private final GetAdminStatsUseCase getStats;
    private final GetCommissionConfigUseCase getCommission;
    private final UpdateCommissionConfigUseCase updateCommission;

    @Override
    public AdminStats getStats() {
        return getStats.getStats();
    }

    @Override
    public CommissionConfig getConfig() {
        return getCommission.getConfig();
    }

    @Override
    public CommissionConfig update(BigDecimal rate, String description) {
        return updateCommission.update(rate, description);
    }
}
