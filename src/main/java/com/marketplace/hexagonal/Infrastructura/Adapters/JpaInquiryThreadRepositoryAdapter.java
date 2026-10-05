package com.marketplace.hexagonal.Infrastructura.Adapters;

import com.marketplace.hexagonal.Domain.Inquiry.Models.InquiryStatus;
import com.marketplace.hexagonal.Domain.Inquiry.Models.InquiryThread;
import com.marketplace.hexagonal.Domain.Inquiry.Models.InquiryThreadId;
import com.marketplace.hexagonal.Domain.Inquiry.Ports.Out.InquiryThreadRepositoryPort;
import com.marketplace.hexagonal.Domain.Product.Models.ProductId;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import com.marketplace.hexagonal.Infrastructura.Entities.Inquiry.InquiryThreadEntity;
import com.marketplace.hexagonal.Infrastructura.Entities.User.UserEntity;
import com.marketplace.hexagonal.Infrastructura.Persistence.JpaInquiryMessageRepository;
import com.marketplace.hexagonal.Infrastructura.Persistence.JpaInquiryThreadRepository;
import com.marketplace.hexagonal.Infrastructura.Persistence.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JpaInquiryThreadRepositoryAdapter implements InquiryThreadRepositoryPort {

    private final JpaInquiryThreadRepository jpaInquiryThreadRepository;
    private final JpaInquiryMessageRepository jpaInquiryMessageRepository;
    private final JpaUserRepository jpaUserRepository;

    @Override
    @Transactional
    public InquiryThread save(InquiryThread thread) {
        UserEntity buyer = findUser(thread.getBuyerId());
        UserEntity seller = findUser(thread.getSellerUserId());

        InquiryThreadEntity entity = InquiryThreadEntity.builder()
                .productId(UUID.fromString(thread.getProductId().getValue()))
                .buyer(buyer)
                .sellerUser(seller)
                .status(thread.getStatus())
                .createdAt(thread.getCreatedAt())
                .lastActivityAt(thread.getLastActivityAt())
                .build();

        return toDomain(jpaInquiryThreadRepository.save(entity));
    }

    @Override
    public Optional<InquiryThread> findById(InquiryThreadId id) {
        return jpaInquiryThreadRepository
                .findById(UUID.fromString(id.getValue()))
                .map(this::toDomain);
    }

    @Override
    public Optional<InquiryThread> findByBuyerIdAndProductId(UserId buyerId, ProductId productId) {
        return jpaInquiryThreadRepository
                .findByProductIdAndBuyer_Id(
                        UUID.fromString(productId.getValue()),
                        UUID.fromString(buyerId.getValue()))
                .map(this::toDomain);
    }

    @Override
    public List<InquiryThread> findByParticipant(UserId userId) {
        UUID uid = UUID.fromString(userId.getValue());

        LinkedHashMap<UUID, InquiryThreadEntity> threads = new LinkedHashMap<>();
        jpaInquiryThreadRepository.findByBuyer_Id(uid).forEach(t -> threads.put(t.getId(), t));
        jpaInquiryThreadRepository.findBySellerUser_Id(uid).forEach(t -> threads.put(t.getId(), t));
        return threads.values().stream().map(this::toDomain).toList();
    }

    @Override
    @Transactional
    public void updateLastActivity(InquiryThreadId id, Instant lastActivityAt) {
        jpaInquiryThreadRepository.updateLastActivity(UUID.fromString(id.getValue()), lastActivityAt);
    }

    @Override
    @Transactional
    public void updateStatus(InquiryThreadId id, InquiryStatus status) {
        jpaInquiryThreadRepository.updateStatus(UUID.fromString(id.getValue()), status);
    }

    @Override
    @Transactional
    public void deleteById(InquiryThreadId id) {
        jpaInquiryThreadRepository.deleteById(UUID.fromString(id.getValue()));
    }

    @Override
    @Transactional
    public int deleteInactiveThreads(Instant cutoff) {
        jpaInquiryMessageRepository.deleteByThread_LastActivityAtBefore(cutoff);
        return jpaInquiryThreadRepository.deleteByLastActivityAtBefore(cutoff);
    }

    private InquiryThread toDomain(InquiryThreadEntity e) {
        return InquiryThread.reconstitute(
                InquiryThreadId.of(e.getId().toString()),
                ProductId.of(e.getProductId().toString()),
                UserId.of(e.getBuyer().getId().toString()),
                UserId.of(e.getSellerUser().getId().toString()),
                e.getStatus(),
                e.getCreatedAt(),
                e.getLastActivityAt());
    }

    private UserEntity findUser(UserId userId) {
        return jpaUserRepository
                .findById(UUID.fromString(userId.getValue()))
                .orElseThrow(() -> new IllegalArgumentException(
                        "User not found: " + userId.getValue()));
    }
}
