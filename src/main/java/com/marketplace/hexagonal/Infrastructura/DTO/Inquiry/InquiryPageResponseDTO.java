package com.marketplace.hexagonal.Infrastructura.DTO.Inquiry;

import java.util.List;

public record InquiryPageResponseDTO(
        List<InquiryMessageDTO> messages,
        int page,
        int size,
        long totalMessages
) {}
