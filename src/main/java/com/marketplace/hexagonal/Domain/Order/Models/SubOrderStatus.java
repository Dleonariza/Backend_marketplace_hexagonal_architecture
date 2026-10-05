package com.marketplace.hexagonal.Domain.Order.Models;

public enum SubOrderStatus {

    PENDING_PAYMENT,

    PAID,

    PROCESSING,

    SHIPPED,

    DELIVERED,

    CANCELLED,

    REFUNDED
}
