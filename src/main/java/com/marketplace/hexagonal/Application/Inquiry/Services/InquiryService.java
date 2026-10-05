package com.marketplace.hexagonal.Application.Inquiry.Services;

import com.marketplace.hexagonal.Domain.Inquiry.Models.InquiryMessage;
import com.marketplace.hexagonal.Domain.Inquiry.Models.InquiryThread;
import com.marketplace.hexagonal.Domain.Inquiry.Models.InquiryThreadId;
import com.marketplace.hexagonal.Domain.Inquiry.Ports.In.*;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class InquiryService {

    private final CreateOrGetInquiryThreadUseCase createOrGetInquiryThread;
    private final SendInquiryMessageUseCase sendInquiryMessage;
    private final GetInquiryMessagesUseCase getInquiryMessages;
    private final GetUserInquiryThreadsUseCase getUserInquiryThreads;
    private final CloseInquiryThreadUseCase closeInquiryThread;

    public InquiryThread getOrCreateThread(UserId buyerId, String productId) {
        return createOrGetInquiryThread.execute(buyerId, productId);
    }

    public InquiryMessage sendMessage(InquiryThreadId threadId, UserId senderId, String senderName, String content) {
        return sendInquiryMessage.execute(threadId, senderId, senderName, content);
    }

    public List<InquiryMessage> getMessages(InquiryThreadId threadId, UserId requesterId, int page, int size) {
        return getInquiryMessages.execute(threadId, requesterId, page, size);
    }

    public long countMessages(InquiryThreadId threadId, UserId requesterId) {
        return getInquiryMessages.count(threadId, requesterId);
    }

    public List<InquiryThread> getUserThreads(UserId userId) {
        return getUserInquiryThreads.execute(userId);
    }

    public InquiryThread closeThread(InquiryThreadId threadId, UserId requesterId) {
        return closeInquiryThread.execute(threadId, requesterId);
    }
}
