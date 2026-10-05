package com.marketplace.hexagonal.Infrastructura.DTO.Chat;

public record SendMessageRequestDTO(
        String roomId,
        String content
) {}
