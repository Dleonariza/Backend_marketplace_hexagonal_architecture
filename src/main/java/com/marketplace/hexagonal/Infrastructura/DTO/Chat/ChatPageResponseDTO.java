package com.marketplace.hexagonal.Infrastructura.DTO.Chat;

import java.util.List;

public record ChatPageResponseDTO(
        List<ChatMessageDTO> messages,
        int page,
        int size,
        long totalMessages
) {}
