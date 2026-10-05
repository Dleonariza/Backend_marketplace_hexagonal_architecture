package com.marketplace.hexagonal.Domain.Inquiry.Models;

import com.marketplace.hexagonal.Domain.Product.Models.ProductId;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import lombok.*;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class InquiryThread {

    private InquiryThreadId id;
    private ProductId productId;
    private UserId buyerId;
    private UserId sellerUserId;
    private InquiryStatus status;
    private Instant createdAt;
    private Instant lastActivityAt;

    public static InquiryThread create(ProductId productId, UserId buyerId, UserId sellerUserId) {
        Instant now = Instant.now();
        return InquiryThread.builder()
                .id(InquiryThreadId.generate())
                .productId(productId)
                .buyerId(buyerId)
                .sellerUserId(sellerUserId)
                .status(InquiryStatus.OPEN)
                .createdAt(now)
                .lastActivityAt(now)
                .build();
    }

    public static InquiryThread reconstitute(InquiryThreadId id,
                                             ProductId productId,
                                             UserId buyerId,
                                             UserId sellerUserId,
                                             InquiryStatus status,
                                             Instant createdAt,
                                             Instant lastActivityAt) {
        InquiryThread t = new InquiryThread();
        t.id = id;
        t.productId = productId;
        t.buyerId = buyerId;
        t.sellerUserId = sellerUserId;
        t.status = status;
        t.createdAt = createdAt;
        t.lastActivityAt = lastActivityAt;
        return t;
    }

    public void touch() {
        this.lastActivityAt = Instant.now();
    }

    public void close() {
        this.status = InquiryStatus.CLOSED;
    }

    public boolean isParticipant(UserId userId) {
        if (buyerId.equals(userId)) return true;
        return sellerUserId != null && sellerUserId.equals(userId);
    }
}
