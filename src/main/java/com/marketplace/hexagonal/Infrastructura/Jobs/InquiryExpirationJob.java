package com.marketplace.hexagonal.Infrastructura.Jobs;

import com.marketplace.hexagonal.Domain.Inquiry.Ports.Out.InquiryThreadRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class InquiryExpirationJob {

    private final InquiryThreadRepositoryPort inquiryThreadRepo;

    @Value("${inquiry.inactivity-minutes:1440}")
    private int inactivityMinutes;

    @Scheduled(fixedRate = 60_000)
    public void expireInactiveThreads() {
        try {
            Instant cutoff = Instant.now().minus(inactivityMinutes, ChronoUnit.MINUTES);
            int deleted = inquiryThreadRepo.deleteInactiveThreads(cutoff);
            if (deleted > 0) {
                log.info("[InquiryExpirationJob] Deleted {} inactive inquiry thread(s) " +
                         "(inactivity threshold: {} min)", deleted, inactivityMinutes);
            }
        } catch (Exception ex) {
            log.error("[InquiryExpirationJob] Error during expiration run.", ex);
        }
    }
}
