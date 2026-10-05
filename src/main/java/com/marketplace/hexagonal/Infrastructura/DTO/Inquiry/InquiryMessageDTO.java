package com.marketplace.hexagonal.Infrastructura.DTO.Inquiry;

import com.marketplace.hexagonal.Domain.Inquiry.Models.InquiryMessage;

import java.time.Instant;

public record InquiryMessageDTO(
        String messageId,
        String threadId,
        String senderId,
        String senderName,
        String content,
        Instant sentAt
) {
    public static InquiryMessageDTO from(InquiryMessage m) {
        return new InquiryMessageDTO(
                m.getId().getValue(),
                m.getThreadId().getValue(),
                m.getSenderId().getValue(),
                m.getSenderName(),
                m.getContent(),
                m.getSentAt());
    }
}
