package com.marketplace.hexagonal.Infrastructura.Adapters;

import com.marketplace.hexagonal.Domain.Admin.Models.AdminStats;
import com.marketplace.hexagonal.Domain.Admin.Ports.Out.AdminStatsRepositoryPort;
import com.marketplace.hexagonal.Domain.User.Models.UserStatus;
import com.marketplace.hexagonal.Infrastructura.Persistence.JpaOrderRepository;
import com.marketplace.hexagonal.Infrastructura.Persistence.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JpaAdminStatsRepositoryAdapter implements AdminStatsRepositoryPort {

    private final JpaOrderRepository jpaOrderRepo;
    private final JpaUserRepository jpaUserRepo;

    @Override
    public AdminStats fetchStats() {
        Instant startOfDay = ZonedDateTime.now(ZoneOffset.UTC)
                .toLocalDate()
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant();

        BigDecimal gmv = jpaOrderRepo.sumGmv();
        long ordersToday = jpaOrderRepo.countOrdersSince(startOfDay);
        long usersToday = jpaUserRepo.countUsersSince(startOfDay);
        long totalActiveUsers = jpaUserRepo.countByStatus(UserStatus.ACTIVE);
        long totalOrders = jpaOrderRepo.countAll();

        Map<String, Long> ordersByStatus = new LinkedHashMap<>();
        jpaOrderRepo.countByStatus().forEach(row ->
                ordersByStatus.put(row[0].toString(), (Long) row[1]));

        return new AdminStats(gmv, ordersToday, usersToday,
                totalActiveUsers, totalOrders, ordersByStatus);
    }
}
