package com.marketplace.hexagonal.Domain.Inquiry.Ports.In;

import com.marketplace.hexagonal.Domain.Inquiry.Models.InquiryMessage;
import com.marketplace.hexagonal.Domain.Inquiry.Models.InquiryThreadId;
import com.marketplace.hexagonal.Domain.User.Models.UserId;

public interface SendInquiryMessageUseCase {

    InquiryMessage execute(InquiryThreadId threadId, UserId senderId, String senderName, String content);
}
