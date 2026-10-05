package com.marketplace.hexagonal.Domain.Admin.Ports.Out;

import com.marketplace.hexagonal.Domain.Admin.Models.AdminStats;

public interface AdminStatsRepositoryPort {
    AdminStats fetchStats();
}
