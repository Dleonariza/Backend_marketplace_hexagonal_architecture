package com.marketplace.hexagonal.Infrastructura.DTO.Inquiry;

public record SendInquiryMessageRequestDTO(
        String threadId,
        String content
) {}
