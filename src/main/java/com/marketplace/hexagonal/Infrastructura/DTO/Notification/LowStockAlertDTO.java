package com.marketplace.hexagonal.Infrastructura.DTO.Notification;

import com.marketplace.hexagonal.Domain.Product.Events.LowStockAlertEvent;

import java.time.Instant;

public record LowStockAlertDTO(
        String type,
        String productId,
        String productName,
        String variantId,
        String sku,
        int currentStock,
        int threshold,
        Instant timestamp
) {
    public static LowStockAlertDTO from(LowStockAlertEvent event) {
        return new LowStockAlertDTO(
                "LOW_STOCK_ALERT",
                event.productId().getValue(),
                event.productName(),
                event.variantId().getValue(),
                event.sku(),
                event.currentStock(),
                LowStockAlertEvent.LOW_STOCK_THRESHOLD,
                Instant.now()
        );
    }
}
