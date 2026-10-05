package com.marketplace.hexagonal.Domain.Inquiry.Ports.In;

import com.marketplace.hexagonal.Domain.Inquiry.Models.InquiryMessage;
import com.marketplace.hexagonal.Domain.Inquiry.Models.InquiryThreadId;
import com.marketplace.hexagonal.Domain.User.Models.UserId;

import java.util.List;

public interface GetInquiryMessagesUseCase {

    List<InquiryMessage> execute(InquiryThreadId threadId, UserId requesterId, int page, int size);

    long count(InquiryThreadId threadId, UserId requesterId);
}
