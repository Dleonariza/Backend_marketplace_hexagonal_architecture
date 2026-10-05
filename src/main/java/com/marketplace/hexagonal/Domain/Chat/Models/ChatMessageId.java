package com.marketplace.hexagonal.Domain.Chat.Models;

import lombok.Getter;

import java.util.UUID;

@Getter
public class ChatMessageId {

    private final String value;

    private ChatMessageId(String value) {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException("ChatMessageId cannot be null or blank");
        this.value = value;
    }

    public static ChatMessageId generate() { return new ChatMessageId(UUID.randomUUID().toString()); }
    public static ChatMessageId of(String value) { return new ChatMessageId(value); }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChatMessageId c)) return false;
        return value.equals(c.value);
    }
    @Override public int hashCode() { return value.hashCode(); }
    @Override public String toString() { return value; }
}
