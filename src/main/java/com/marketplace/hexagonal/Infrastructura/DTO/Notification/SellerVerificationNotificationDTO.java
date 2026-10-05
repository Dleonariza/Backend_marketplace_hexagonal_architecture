package com.marketplace.hexagonal.Infrastructura.DTO.Notification;

import com.marketplace.hexagonal.Domain.Seller.Events.SellerVerificationCompletedEvent;

import java.time.Instant;

public record SellerVerificationNotificationDTO(
        String type,
        String storeName,
        String status,
        String reason,
        Instant timestamp
) {
    public static SellerVerificationNotificationDTO from(SellerVerificationCompletedEvent event) {
        boolean approved = switch (event.newStatus()) {
            case ACTIVE -> true;
            default -> false;
        };
        return new SellerVerificationNotificationDTO(
                approved ? "VERIFICATION_APPROVED" : "VERIFICATION_REJECTED",
                event.storeName(),
                event.newStatus().name(),
                event.reason(),
                Instant.now()
        );
    }
}
