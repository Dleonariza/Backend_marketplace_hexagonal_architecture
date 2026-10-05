package com.marketplace.hexagonal.Domain.Chat.Models;

import lombok.Getter;

import java.util.UUID;

@Getter
public class ChatRoomId {

    private final String value;

    private ChatRoomId(String value) {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException("ChatRoomId cannot be null or blank");
        this.value = value;
    }

    public static ChatRoomId generate() { return new ChatRoomId(UUID.randomUUID().toString()); }
    public static ChatRoomId of(String value) { return new ChatRoomId(value); }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChatRoomId c)) return false;
        return value.equals(c.value);
    }
    @Override public int hashCode() { return value.hashCode(); }
    @Override public String toString() { return value; }
}
