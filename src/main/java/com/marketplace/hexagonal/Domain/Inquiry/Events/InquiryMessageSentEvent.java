package com.marketplace.hexagonal.Domain.Inquiry.Events;

import com.marketplace.hexagonal.Domain.Inquiry.Models.InquiryMessage;
import com.marketplace.hexagonal.Domain.Inquiry.Models.InquiryThreadId;
import com.marketplace.hexagonal.Domain.User.Models.UserId;

public record InquiryMessageSentEvent(
        InquiryThreadId threadId,
        InquiryMessage message,
        UserId buyerId,
        UserId sellerUserId
) {}
