package com.marketplace.hexagonal.Application.Inquiry;

import com.marketplace.hexagonal.Application.Inquiry.UseCases.CloseInquiryThreadUseCaseImpl;
import com.marketplace.hexagonal.Domain.Inquiry.Exceptions.InquiryThreadAccessDeniedException;
import com.marketplace.hexagonal.Domain.Inquiry.Exceptions.InquiryThreadNotFoundException;
import com.marketplace.hexagonal.Domain.Inquiry.Models.InquiryStatus;
import com.marketplace.hexagonal.Domain.Inquiry.Models.InquiryThread;
import com.marketplace.hexagonal.Domain.Inquiry.Models.InquiryThreadId;
import com.marketplace.hexagonal.Domain.Inquiry.Ports.Out.InquiryThreadRepositoryPort;
import com.marketplace.hexagonal.Domain.Product.Models.ProductId;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CloseInquiryThreadUseCaseImplTest {

    @Mock InquiryThreadRepositoryPort threadRepo;
    CloseInquiryThreadUseCaseImpl useCase;

    private static final UserId BUYER_ID = UserId.of("550e8400-e29b-41d4-a716-446655440000");
    private static final UserId SELLER_UID = UserId.of("bc66d237-f54d-4bad-b521-0eba3b06304c");
    private static final UserId STRANGER = UserId.of("00000000-0000-0000-0000-000000000099");
    private static final ProductId PRODUCT_ID = ProductId.of("aa000000-0000-0000-0000-000000000001");

    @BeforeEach
    void setUp() {
        useCase = new CloseInquiryThreadUseCaseImpl(threadRepo);
    }

    @Test
    void execute_participant_closesThread() {
        InquiryThread thread = InquiryThread.create(PRODUCT_ID, BUYER_ID, SELLER_UID);
        when(threadRepo.findById(thread.getId())).thenReturn(Optional.of(thread));

        InquiryThread result = useCase.execute(thread.getId(), SELLER_UID);

        assertThat(result.getStatus()).isEqualTo(InquiryStatus.CLOSED);
        verify(threadRepo).updateStatus(thread.getId(), InquiryStatus.CLOSED);
    }

    @Test
    void execute_nonParticipant_throwsAccessDenied() {
        InquiryThread thread = InquiryThread.create(PRODUCT_ID, BUYER_ID, SELLER_UID);
        when(threadRepo.findById(thread.getId())).thenReturn(Optional.of(thread));

        assertThatThrownBy(() -> useCase.execute(thread.getId(), STRANGER))
                .isInstanceOf(InquiryThreadAccessDeniedException.class);

        verify(threadRepo, never()).updateStatus(any(), any());
    }

    @Test
    void execute_threadNotFound_throwsNotFound() {
        InquiryThreadId missingId = InquiryThreadId.generate();
        when(threadRepo.findById(missingId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(missingId, BUYER_ID))
                .isInstanceOf(InquiryThreadNotFoundException.class);

        verify(threadRepo, never()).updateStatus(any(), any());
    }
}
