package com.marketplace.hexagonal.Infrastructura.Adapters;

import com.marketplace.hexagonal.Domain.Inquiry.Models.InquiryMessage;
import com.marketplace.hexagonal.Domain.Inquiry.Models.InquiryMessageId;
import com.marketplace.hexagonal.Domain.Inquiry.Models.InquiryThreadId;
import com.marketplace.hexagonal.Domain.Inquiry.Ports.Out.InquiryMessageRepositoryPort;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import com.marketplace.hexagonal.Infrastructura.Entities.Inquiry.InquiryMessageEntity;
import com.marketplace.hexagonal.Infrastructura.Entities.Inquiry.InquiryThreadEntity;
import com.marketplace.hexagonal.Infrastructura.Entities.User.UserEntity;
import com.marketplace.hexagonal.Infrastructura.Persistence.JpaInquiryMessageRepository;
import com.marketplace.hexagonal.Infrastructura.Persistence.JpaInquiryThreadRepository;
import com.marketplace.hexagonal.Infrastructura.Persistence.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JpaInquiryMessageRepositoryAdapter implements InquiryMessageRepositoryPort {

    private final JpaInquiryMessageRepository jpaInquiryMessageRepository;
    private final JpaInquiryThreadRepository jpaInquiryThreadRepository;
    private final JpaUserRepository jpaUserRepository;

    @Override
    @Transactional
    public InquiryMessage save(InquiryMessage message) {
        InquiryThreadEntity thread = jpaInquiryThreadRepository
                .findById(UUID.fromString(message.getThreadId().getValue()))
                .orElseThrow(() -> new IllegalArgumentException(
                        "Inquiry thread not found: " + message.getThreadId().getValue()));

        UserEntity sender = jpaUserRepository
                .findById(UUID.fromString(message.getSenderId().getValue()))
                .orElseThrow(() -> new IllegalArgumentException(
                        "Sender not found: " + message.getSenderId().getValue()));

        InquiryMessageEntity entity = InquiryMessageEntity.builder()
                .thread(thread)
                .sender(sender)
                .senderName(message.getSenderName())
                .content(message.getContent())
                .sentAt(message.getSentAt())
                .build();

        return toDomain(jpaInquiryMessageRepository.save(entity));
    }

    @Override
    public List<InquiryMessage> findByThreadId(InquiryThreadId threadId, int page, int size) {
        return jpaInquiryMessageRepository
                .findByThread_IdOrderBySentAtDesc(
                        UUID.fromString(threadId.getValue()),
                        PageRequest.of(page, size))
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public long countByThreadId(InquiryThreadId threadId) {
        return jpaInquiryMessageRepository.countByThread_Id(UUID.fromString(threadId.getValue()));
    }

    @Override
    public List<InquiryMessage> findAllByThreadIdOrderBySentAtAsc(InquiryThreadId threadId) {
        return jpaInquiryMessageRepository
                .findByThread_IdOrderBySentAtAsc(UUID.fromString(threadId.getValue()))
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private InquiryMessage toDomain(InquiryMessageEntity e) {
        return InquiryMessage.reconstitute(
                InquiryMessageId.of(e.getId().toString()),
                InquiryThreadId.of(e.getThread().getId().toString()),
                UserId.of(e.getSender().getId().toString()),
                e.getSenderName(),
                e.getContent(),
                e.getSentAt());
    }
}
