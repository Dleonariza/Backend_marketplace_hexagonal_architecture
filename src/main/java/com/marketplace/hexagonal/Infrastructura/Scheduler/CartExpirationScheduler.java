package com.marketplace.hexagonal.Infrastructura.Scheduler;

import com.marketplace.hexagonal.Application.Cart.UseCases.ExpireCartsUseCaseImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CartExpirationScheduler {

    private final ExpireCartsUseCaseImpl expireCartsUseCase;

    @Scheduled(cron = "0 0 2 * * *")
    public void expireAbandonedCarts() {
        int count = expireCartsUseCase.expireCarts();
        log.info("Cart TTL job: expired {} cart(s) with TTL > {} days",
                count, com.marketplace.hexagonal.Domain.Cart.Models.Cart.TTL_DAYS);
    }
}
