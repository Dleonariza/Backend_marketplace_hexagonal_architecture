package com.marketplace.hexagonal.Domain.Inquiry.Models;

import com.marketplace.hexagonal.Domain.User.Models.UserId;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class InquiryMessageTest {

    private static final InquiryThreadId THREAD_ID = InquiryThreadId.generate();
    private static final UserId SENDER_ID = UserId.of("550e8400-e29b-41d4-a716-446655440000");

    @Test
    void create_valid_buildsMessage() {
        InquiryMessage message = InquiryMessage.create(THREAD_ID, SENDER_ID, "Juan", "  Is this in stock?  ");

        assertThat(message.getId()).isNotNull();
        assertThat(message.getThreadId()).isEqualTo(THREAD_ID);
        assertThat(message.getSenderId()).isEqualTo(SENDER_ID);
        assertThat(message.getSenderName()).isEqualTo("Juan");
        assertThat(message.getContent()).isEqualTo("Is this in stock?");
        assertThat(message.getSentAt()).isNotNull();
    }

    @Test
    void create_blankContent_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> InquiryMessage.create(THREAD_ID, SENDER_ID, "Juan", "   "))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
