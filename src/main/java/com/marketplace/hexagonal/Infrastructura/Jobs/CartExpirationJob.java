package com.marketplace.hexagonal.Infrastructura.Jobs;

import com.marketplace.hexagonal.Application.Cart.UseCases.ExpireCartsUseCaseImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CartExpirationJob {

    private final ExpireCartsUseCaseImpl expireCarts;

    @Scheduled(cron = "${cart.expiration.cron:0 0 2 * * *}")
    public void run() {
        log.info("[CartExpirationJob] Starting abandoned cart expiration...");
        try {
            int expired = expireCarts.expireCarts();
            log.info("[CartExpirationJob] Finished - {} cart(s) expired.", expired);
        } catch (Exception e) {
            log.error("[CartExpirationJob] Error during expiration run.", e);
        }
    }
}
