package com.marketplace.hexagonal.Application.Inquiry;

import com.marketplace.hexagonal.Application.Inquiry.UseCases.GetUserInquiryThreadsUseCaseImpl;
import com.marketplace.hexagonal.Domain.Inquiry.Models.InquiryThread;
import com.marketplace.hexagonal.Domain.Inquiry.Ports.Out.InquiryThreadRepositoryPort;
import com.marketplace.hexagonal.Domain.Product.Models.ProductId;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetUserInquiryThreadsUseCaseImplTest {

    @Mock InquiryThreadRepositoryPort threadRepo;
    GetUserInquiryThreadsUseCaseImpl useCase;

    private static final UserId BUYER_ID = UserId.of("550e8400-e29b-41d4-a716-446655440000");
    private static final UserId SELLER_UID = UserId.of("bc66d237-f54d-4bad-b521-0eba3b06304c");
    private static final ProductId PRODUCT_ID = ProductId.of("aa000000-0000-0000-0000-000000000001");

    @BeforeEach
    void setUp() {
        useCase = new GetUserInquiryThreadsUseCaseImpl(threadRepo);
    }

    @Test
    void execute_delegatesToFindByParticipant() {
        InquiryThread thread = InquiryThread.create(PRODUCT_ID, BUYER_ID, SELLER_UID);
        when(threadRepo.findByParticipant(BUYER_ID)).thenReturn(List.of(thread));

        List<InquiryThread> result = useCase.execute(BUYER_ID);

        assertThat(result).containsExactly(thread);
    }
}
