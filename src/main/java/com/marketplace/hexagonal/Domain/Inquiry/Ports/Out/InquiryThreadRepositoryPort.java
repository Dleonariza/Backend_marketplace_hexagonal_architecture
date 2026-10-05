package com.marketplace.hexagonal.Domain.Inquiry.Ports.Out;

import com.marketplace.hexagonal.Domain.Inquiry.Models.InquiryThread;
import com.marketplace.hexagonal.Domain.Inquiry.Models.InquiryThreadId;
import com.marketplace.hexagonal.Domain.Product.Models.ProductId;
import com.marketplace.hexagonal.Domain.User.Models.UserId;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface InquiryThreadRepositoryPort {

    InquiryThread save(InquiryThread thread);

    Optional<InquiryThread> findById(InquiryThreadId id);

    Optional<InquiryThread> findByBuyerIdAndProductId(UserId buyerId, ProductId productId);

    List<InquiryThread> findByParticipant(UserId userId);

    void updateLastActivity(InquiryThreadId id, Instant lastActivityAt);

    void updateStatus(InquiryThreadId id, com.marketplace.hexagonal.Domain.Inquiry.Models.InquiryStatus status);

    void deleteById(InquiryThreadId id);

    int deleteInactiveThreads(Instant cutoff);
}
