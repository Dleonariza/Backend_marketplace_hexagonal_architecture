package com.marketplace.hexagonal.Application.Inquiry.UseCases;

import com.marketplace.hexagonal.Domain.Inquiry.Models.InquiryThread;
import com.marketplace.hexagonal.Domain.Inquiry.Ports.In.GetUserInquiryThreadsUseCase;
import com.marketplace.hexagonal.Domain.Inquiry.Ports.Out.InquiryThreadRepositoryPort;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class GetUserInquiryThreadsUseCaseImpl implements GetUserInquiryThreadsUseCase {

    private final InquiryThreadRepositoryPort inquiryThreadRepo;

    @Override
    public List<InquiryThread> execute(UserId userId) {
        return inquiryThreadRepo.findByParticipant(userId);
    }
}
