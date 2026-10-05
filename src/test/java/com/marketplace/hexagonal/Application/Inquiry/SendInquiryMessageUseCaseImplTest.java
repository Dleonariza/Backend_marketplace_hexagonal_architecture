package com.marketplace.hexagonal.Application.Inquiry;

import com.marketplace.hexagonal.Application.Inquiry.UseCases.SendInquiryMessageUseCaseImpl;
import com.marketplace.hexagonal.Domain.Inquiry.Events.InquiryMessageSentEvent;
import com.marketplace.hexagonal.Domain.Inquiry.Exceptions.InquiryThreadAccessDeniedException;
import com.marketplace.hexagonal.Domain.Inquiry.Exceptions.InquiryThreadNotFoundException;
import com.marketplace.hexagonal.Domain.Inquiry.Models.InquiryMessage;
import com.marketplace.hexagonal.Domain.Inquiry.Models.InquiryThread;
import com.marketplace.hexagonal.Domain.Inquiry.Ports.Out.InquiryMessageRepositoryPort;
import com.marketplace.hexagonal.Domain.Inquiry.Ports.Out.InquiryThreadRepositoryPort;
import com.marketplace.hexagonal.Domain.Product.Models.ProductId;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SendInquiryMessageUseCaseImplTest {

    @Mock InquiryThreadRepositoryPort threadRepo;
    @Mock InquiryMessageRepositoryPort messageRepo;
    @Mock ApplicationEventPublisher eventPublisher;
    SendInquiryMessageUseCaseImpl useCase;

    private static final UserId BUYER_ID = UserId.of("550e8400-e29b-41d4-a716-446655440000");
    private static final UserId SELLER_UID = UserId.of("bc66d237-f54d-4bad-b521-0eba3b06304c");
    private static final UserId STRANGER = UserId.of("00000000-0000-0000-0000-000000000099");
    private static final ProductId PRODUCT_ID = ProductId.of("aa000000-0000-0000-0000-000000000001");

    @BeforeEach
    void setUp() {
        useCase = new SendInquiryMessageUseCaseImpl(threadRepo, messageRepo, eventPublisher);
    }

    @Test
    void execute_participantSends_savesMessageTouchesThreadAndPublishesEvent() {
        InquiryThread thread = InquiryThread.create(PRODUCT_ID, BUYER_ID, SELLER_UID);
        when(threadRepo.findById(thread.getId())).thenReturn(Optional.of(thread));
        when(messageRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        InquiryMessage result = useCase.execute(thread.getId(), BUYER_ID, "Juan", "Is this in stock?");

        assertThat(result.getContent()).isEqualTo("Is this in stock?");
        verify(threadRepo).updateLastActivity(eq(thread.getId()), any());
        verify(eventPublisher).publishEvent(any(InquiryMessageSentEvent.class));
    }

    @Test
    void execute_nonParticipant_throwsAccessDenied() {
        InquiryThread thread = InquiryThread.create(PRODUCT_ID, BUYER_ID, SELLER_UID);
        when(threadRepo.findById(thread.getId())).thenReturn(Optional.of(thread));

        assertThatThrownBy(() -> useCase.execute(thread.getId(), STRANGER, "Hacker", "Hi"))
                .isInstanceOf(InquiryThreadAccessDeniedException.class);

        verify(messageRepo, never()).save(any());
    }

    @Test
    void execute_threadNotFound_throwsNotFound() {
        com.marketplace.hexagonal.Domain.Inquiry.Models.InquiryThreadId missing =
                com.marketplace.hexagonal.Domain.Inquiry.Models.InquiryThreadId.generate();
        when(threadRepo.findById(missing)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(missing, BUYER_ID, "Juan", "Hi"))
                .isInstanceOf(InquiryThreadNotFoundException.class);
    }
}
