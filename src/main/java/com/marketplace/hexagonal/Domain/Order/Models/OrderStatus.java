package com.marketplace.hexagonal.Domain.Order.Models;

public enum OrderStatus {

    PENDING_PAYMENT,

    PAID,

    PROCESSING,

    SHIPPED,

    DELIVERED,

    CANCELLED,

    REFUNDED
}
