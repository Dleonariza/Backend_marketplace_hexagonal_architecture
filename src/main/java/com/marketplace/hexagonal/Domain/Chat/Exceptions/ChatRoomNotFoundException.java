package com.marketplace.hexagonal.Domain.Chat.Exceptions;

public class ChatRoomNotFoundException extends RuntimeException {
    public ChatRoomNotFoundException(String roomId) {
        super("Chat room not found: " + roomId);
    }
}
