package com.marketplace.hexagonal.Domain.Inquiry.Models;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class InquiryThreadIdTest {

    @Test
    void generate_producesUniqueNonBlankIds() {
        InquiryThreadId a = InquiryThreadId.generate();
        InquiryThreadId b = InquiryThreadId.generate();

        assertThat(a.getValue()).isNotBlank();
        assertThat(a).isNotEqualTo(b);
    }

    @Test
    void of_blankValue_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> InquiryThreadId.of(" "))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void equals_sameValue_returnsTrue() {
        InquiryThreadId a = InquiryThreadId.of("11111111-1111-1111-1111-111111111111");
        InquiryThreadId b = InquiryThreadId.of("11111111-1111-1111-1111-111111111111");

        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }
}
