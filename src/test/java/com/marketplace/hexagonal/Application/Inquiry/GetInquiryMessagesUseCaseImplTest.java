package com.marketplace.hexagonal.Application.Inquiry;

import com.marketplace.hexagonal.Application.Inquiry.UseCases.GetInquiryMessagesUseCaseImpl;
import com.marketplace.hexagonal.Domain.Inquiry.Exceptions.InquiryThreadAccessDeniedException;
import com.marketplace.hexagonal.Domain.Inquiry.Exceptions.InquiryThreadNotFoundException;
import com.marketplace.hexagonal.Domain.Inquiry.Models.InquiryThread;
import com.marketplace.hexagonal.Domain.Inquiry.Models.InquiryThreadId;
import com.marketplace.hexagonal.Domain.Inquiry.Ports.Out.InquiryMessageRepositoryPort;
import com.marketplace.hexagonal.Domain.Inquiry.Ports.Out.InquiryThreadRepositoryPort;
import com.marketplace.hexagonal.Domain.Product.Models.ProductId;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetInquiryMessagesUseCaseImplTest {

    @Mock InquiryThreadRepositoryPort threadRepo;
    @Mock InquiryMessageRepositoryPort messageRepo;
    GetInquiryMessagesUseCaseImpl useCase;

    private static final UserId BUYER_ID = UserId.of("550e8400-e29b-41d4-a716-446655440000");
    private static final UserId SELLER_UID = UserId.of("bc66d237-f54d-4bad-b521-0eba3b06304c");
    private static final UserId STRANGER = UserId.of("00000000-0000-0000-0000-000000000099");
    private static final ProductId PRODUCT_ID = ProductId.of("aa000000-0000-0000-0000-000000000001");

    @BeforeEach
    void setUp() {
        useCase = new GetInquiryMessagesUseCaseImpl(threadRepo, messageRepo);
    }

    @Test
    void execute_participant_returnsPage() {
        InquiryThread thread = InquiryThread.create(PRODUCT_ID, BUYER_ID, SELLER_UID);
        when(threadRepo.findById(thread.getId())).thenReturn(Optional.of(thread));
        when(messageRepo.findByThreadId(thread.getId(), 0, 20)).thenReturn(List.of());

        List<?> result = useCase.execute(thread.getId(), BUYER_ID, 0, 20);

        assertThat(result).isEmpty();
    }

    @Test
    void execute_nonParticipant_throwsAccessDenied() {
        InquiryThread thread = InquiryThread.create(PRODUCT_ID, BUYER_ID, SELLER_UID);
        when(threadRepo.findById(thread.getId())).thenReturn(Optional.of(thread));

        assertThatThrownBy(() -> useCase.execute(thread.getId(), STRANGER, 0, 20))
                .isInstanceOf(InquiryThreadAccessDeniedException.class);
    }

    @Test
    void count_participant_delegatesToRepo() {
        InquiryThread thread = InquiryThread.create(PRODUCT_ID, BUYER_ID, SELLER_UID);
        when(threadRepo.findById(thread.getId())).thenReturn(Optional.of(thread));
        when(messageRepo.countByThreadId(thread.getId())).thenReturn(3L);

        assertThat(useCase.count(thread.getId(), SELLER_UID)).isEqualTo(3L);
    }

    @Test
    void execute_threadNotFound_throwsNotFound() {
        InquiryThreadId missingId = InquiryThreadId.generate();
        when(threadRepo.findById(missingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(missingId, BUYER_ID, 0, 20))
                .isInstanceOf(InquiryThreadNotFoundException.class);
    }

    @Test
    void count_nonParticipant_throwsAccessDenied() {
        InquiryThread thread = InquiryThread.create(PRODUCT_ID, BUYER_ID, SELLER_UID);
        when(threadRepo.findById(thread.getId())).thenReturn(Optional.of(thread));

        assertThatThrownBy(() -> useCase.count(thread.getId(), STRANGER))
                .isInstanceOf(InquiryThreadAccessDeniedException.class);
    }
}
