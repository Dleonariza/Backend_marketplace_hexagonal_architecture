package com.marketplace.hexagonal.Domain.Inquiry.Ports.In;

import com.marketplace.hexagonal.Domain.Inquiry.Models.InquiryThread;
import com.marketplace.hexagonal.Domain.User.Models.UserId;

import java.util.List;

public interface GetUserInquiryThreadsUseCase {

    List<InquiryThread> execute(UserId userId);
}
