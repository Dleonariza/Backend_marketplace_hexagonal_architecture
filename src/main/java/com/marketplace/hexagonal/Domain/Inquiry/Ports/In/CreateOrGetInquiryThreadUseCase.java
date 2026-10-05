package com.marketplace.hexagonal.Domain.Inquiry.Ports.In;

import com.marketplace.hexagonal.Domain.Inquiry.Models.InquiryThread;
import com.marketplace.hexagonal.Domain.User.Models.UserId;

public interface CreateOrGetInquiryThreadUseCase {

    InquiryThread execute(UserId buyerId, String productId);
}
