package com.marketplace.hexagonal.Application.Admin.UseCases;

import com.marketplace.hexagonal.Domain.Admin.Models.AdminStats;
import com.marketplace.hexagonal.Domain.Admin.Ports.In.GetAdminStatsUseCase;
import com.marketplace.hexagonal.Domain.Admin.Ports.Out.AdminStatsRepositoryPort;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GetAdminStatsUseCaseImpl implements GetAdminStatsUseCase {

    private final AdminStatsRepositoryPort statsRepo;

    @Override
    public AdminStats getStats() {
        return statsRepo.fetchStats();
    }
}
