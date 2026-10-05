package com.marketplace.hexagonal.Application.Inquiry.UseCases;

import com.marketplace.hexagonal.Domain.Inquiry.Events.InquiryMessageSentEvent;
import com.marketplace.hexagonal.Domain.Inquiry.Exceptions.InquiryThreadAccessDeniedException;
import com.marketplace.hexagonal.Domain.Inquiry.Exceptions.InquiryThreadNotFoundException;
import com.marketplace.hexagonal.Domain.Inquiry.Models.InquiryMessage;
import com.marketplace.hexagonal.Domain.Inquiry.Models.InquiryThread;
import com.marketplace.hexagonal.Domain.Inquiry.Models.InquiryThreadId;
import com.marketplace.hexagonal.Domain.Inquiry.Ports.In.SendInquiryMessageUseCase;
import com.marketplace.hexagonal.Domain.Inquiry.Ports.Out.InquiryMessageRepositoryPort;
import com.marketplace.hexagonal.Domain.Inquiry.Ports.Out.InquiryThreadRepositoryPort;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;

@RequiredArgsConstructor
public class SendInquiryMessageUseCaseImpl implements SendInquiryMessageUseCase {

    private final InquiryThreadRepositoryPort inquiryThreadRepo;
    private final InquiryMessageRepositoryPort inquiryMessageRepo;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public InquiryMessage execute(InquiryThreadId threadId,
                                  UserId senderId,
                                  String senderName,
                                  String content) {
        InquiryThread thread = inquiryThreadRepo.findById(threadId)
                .orElseThrow(() -> new InquiryThreadNotFoundException(threadId.getValue()));

        if (!thread.isParticipant(senderId))
            throw new InquiryThreadAccessDeniedException();

        InquiryMessage message = InquiryMessage.create(threadId, senderId, senderName, content);
        InquiryMessage saved = inquiryMessageRepo.save(message);

        thread.touch();
        inquiryThreadRepo.updateLastActivity(threadId, thread.getLastActivityAt());

        eventPublisher.publishEvent(new InquiryMessageSentEvent(
                threadId, saved, thread.getBuyerId(), thread.getSellerUserId()));

        return saved;
    }
}
