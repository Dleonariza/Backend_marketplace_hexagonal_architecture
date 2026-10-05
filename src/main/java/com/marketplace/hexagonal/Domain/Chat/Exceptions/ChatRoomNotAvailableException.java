package com.marketplace.hexagonal.Domain.Chat.Exceptions;

public class ChatRoomNotAvailableException extends RuntimeException {
    public ChatRoomNotAvailableException(String message) {
        super(message);
    }
}
