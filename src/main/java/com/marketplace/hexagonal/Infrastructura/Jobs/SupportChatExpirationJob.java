package com.marketplace.hexagonal.Infrastructura.Jobs;

import com.marketplace.hexagonal.Domain.Chat.Ports.Out.ChatRoomRepositoryPort;
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
public class SupportChatExpirationJob {

    private final ChatRoomRepositoryPort chatRoomRepo;

    @Value("${chat.support.inactivity-minutes:5}")
    private int inactivityMinutes;

    @Scheduled(fixedRate = 60_000)
    public void expireInactiveSupportRooms() {
        try {
            Instant cutoff = Instant.now().minus(inactivityMinutes, ChronoUnit.MINUTES);
            int deleted = chatRoomRepo.deleteInactiveSupportRooms(cutoff);
            if (deleted > 0) {
                log.info("[SupportChatExpirationJob] Deleted {} inactive support room(s) " +
                         "(inactivity threshold: {} min)", deleted, inactivityMinutes);
            }
        } catch (Exception ex) {
            log.error("[SupportChatExpirationJob] Error during expiration run.", ex);
        }
    }
}
