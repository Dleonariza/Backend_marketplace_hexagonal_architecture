package com.marketplace.hexagonal.Application.Inquiry.UseCases;

import com.marketplace.hexagonal.Domain.Inquiry.Exceptions.InquiryThreadAccessDeniedException;
import com.marketplace.hexagonal.Domain.Inquiry.Exceptions.InquiryThreadNotFoundException;
import com.marketplace.hexagonal.Domain.Inquiry.Models.InquiryThread;
import com.marketplace.hexagonal.Domain.Inquiry.Models.InquiryThreadId;
import com.marketplace.hexagonal.Domain.Inquiry.Ports.In.CloseInquiryThreadUseCase;
import com.marketplace.hexagonal.Domain.Inquiry.Ports.Out.InquiryThreadRepositoryPort;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CloseInquiryThreadUseCaseImpl implements CloseInquiryThreadUseCase {

    private final InquiryThreadRepositoryPort inquiryThreadRepo;

    @Override
    public InquiryThread execute(InquiryThreadId threadId, UserId requesterId) {
        InquiryThread thread = inquiryThreadRepo.findById(threadId)
                .orElseThrow(() -> new InquiryThreadNotFoundException(threadId.getValue()));

        if (!thread.isParticipant(requesterId))
            throw new InquiryThreadAccessDeniedException();

        thread.close();
        inquiryThreadRepo.updateStatus(threadId, thread.getStatus());
        return thread;
    }
}
