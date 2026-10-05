package com.marketplace.hexagonal.Application.Inquiry.UseCases;

import com.marketplace.hexagonal.Domain.Inquiry.Exceptions.InquiryThreadAccessDeniedException;
import com.marketplace.hexagonal.Domain.Inquiry.Exceptions.InquiryThreadNotFoundException;
import com.marketplace.hexagonal.Domain.Inquiry.Models.InquiryMessage;
import com.marketplace.hexagonal.Domain.Inquiry.Models.InquiryThread;
import com.marketplace.hexagonal.Domain.Inquiry.Models.InquiryThreadId;
import com.marketplace.hexagonal.Domain.Inquiry.Ports.In.GetInquiryMessagesUseCase;
import com.marketplace.hexagonal.Domain.Inquiry.Ports.Out.InquiryMessageRepositoryPort;
import com.marketplace.hexagonal.Domain.Inquiry.Ports.Out.InquiryThreadRepositoryPort;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class GetInquiryMessagesUseCaseImpl implements GetInquiryMessagesUseCase {

    private final InquiryThreadRepositoryPort inquiryThreadRepo;
    private final InquiryMessageRepositoryPort inquiryMessageRepo;

    @Override
    public List<InquiryMessage> execute(InquiryThreadId threadId, UserId requesterId, int page, int size) {
        InquiryThread thread = requireParticipant(threadId, requesterId);
        return inquiryMessageRepo.findByThreadId(thread.getId(), page, size);
    }

    @Override
    public long count(InquiryThreadId threadId, UserId requesterId) {
        InquiryThread thread = requireParticipant(threadId, requesterId);
        return inquiryMessageRepo.countByThreadId(thread.getId());
    }

    private InquiryThread requireParticipant(InquiryThreadId threadId, UserId requesterId) {
        InquiryThread thread = inquiryThreadRepo.findById(threadId)
                .orElseThrow(() -> new InquiryThreadNotFoundException(threadId.getValue()));
        if (!thread.isParticipant(requesterId))
            throw new InquiryThreadAccessDeniedException();
        return thread;
    }
}
