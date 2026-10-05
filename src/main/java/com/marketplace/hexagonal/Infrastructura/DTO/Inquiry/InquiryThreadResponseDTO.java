package com.marketplace.hexagonal.Infrastructura.DTO.Inquiry;

import com.marketplace.hexagonal.Domain.Inquiry.Models.InquiryStatus;
import com.marketplace.hexagonal.Domain.Inquiry.Models.InquiryThread;

import java.time.Instant;

public record InquiryThreadResponseDTO(
        String threadId,
        String productId,
        String buyerId,
        String sellerUserId,
        InquiryStatus status,
        Instant createdAt
) {
    public static InquiryThreadResponseDTO from(InquiryThread thread) {
        return new InquiryThreadResponseDTO(
                thread.getId().getValue(),
                thread.getProductId().getValue(),
                thread.getBuyerId().getValue(),
                thread.getSellerUserId().getValue(),
                thread.getStatus(),
                thread.getCreatedAt());
    }
}
