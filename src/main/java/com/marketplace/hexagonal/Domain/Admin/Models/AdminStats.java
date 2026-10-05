package com.marketplace.hexagonal.Domain.Admin.Models;

import java.math.BigDecimal;
import java.util.Map;

public record AdminStats(
        BigDecimal gmv,
        long ordersToday,
        long usersToday,
        long totalActiveUsers,
        long totalOrdersAllTime,
        Map<String, Long> ordersByStatus
) {}
