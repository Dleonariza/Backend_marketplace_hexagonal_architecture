package com.marketplace.hexagonal.Domain.User.Models;

public enum UserStatus {
    ACTIVE,
    INACTIVE,
    SUSPENDED,
    BLOCKED,
    PENDING_VERIFICATION,
    DELETED;

    public boolean isActive() { return this == ACTIVE; }
    public boolean canLogin() { return this == ACTIVE; }
    public boolean canBeVerified() { return this == PENDING_VERIFICATION; }
}
