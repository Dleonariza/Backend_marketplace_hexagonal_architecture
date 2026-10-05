package com.marketplace.hexagonal.Domain.Chat.Exceptions;

public class ChatRoomAccessDeniedException extends RuntimeException {
    public ChatRoomAccessDeniedException() {
        super("You are not a participant of this chat room.");
    }
}
