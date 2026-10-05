package com.marketplace.hexagonal.Domain.Inquiry.Models;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class InquiryMessageIdTest {

    @Test
    void generate_producesUniqueNonBlankIds() {
        InquiryMessageId a = InquiryMessageId.generate();
        InquiryMessageId b = InquiryMessageId.generate();

        assertThat(a.getValue()).isNotBlank();
        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void of_blankValue_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> InquiryMessageId.of(""))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
