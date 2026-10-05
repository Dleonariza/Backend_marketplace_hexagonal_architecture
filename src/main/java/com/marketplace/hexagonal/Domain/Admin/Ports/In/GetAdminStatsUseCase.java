package com.marketplace.hexagonal.Domain.Admin.Ports.In;

import com.marketplace.hexagonal.Domain.Admin.Models.AdminStats;

public interface GetAdminStatsUseCase {
    AdminStats getStats();
}
