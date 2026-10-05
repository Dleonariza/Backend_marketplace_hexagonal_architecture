package com.marketplace.hexagonal.Infrastructura.DTO.Admin;

import com.marketplace.hexagonal.Domain.Admin.Models.AdminStats;

import java.math.BigDecimal;
import java.util.Map;

public record AdminDashboardResponseDTO(
        BigDecimal gmv,
        long ordersToday,
        long usersToday,
        long totalActiveUsers,
        long totalOrdersAllTime,
        Map<String, Long> ordersByStatus
) {
    public static AdminDashboardResponseDTO from(AdminStats stats) {
        return new AdminDashboardResponseDTO(
                stats.gmv(),
                stats.ordersToday(),
                stats.usersToday(),
                stats.totalActiveUsers(),
                stats.totalOrdersAllTime(),
                stats.ordersByStatus()
        );
    }
}
