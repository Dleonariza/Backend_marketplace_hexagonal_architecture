package com.marketplace.hexagonal.Domain.Inquiry.Models;

import com.marketplace.hexagonal.Domain.Product.Models.ProductId;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.*;

class InquiryThreadTest {

    private static final ProductId PRODUCT_ID = ProductId.of("aa000000-0000-0000-0000-000000000001");
    private static final UserId BUYER_ID = UserId.of("550e8400-e29b-41d4-a716-446655440000");
    private static final UserId SELLER_UID = UserId.of("bc66d237-f54d-4bad-b521-0eba3b06304c");
    private static final UserId STRANGER_ID = UserId.of("00000000-0000-0000-0000-000000000099");

    @Test
    void create_setsOpenStatusAndTimestamps() {
        InquiryThread thread = InquiryThread.create(PRODUCT_ID, BUYER_ID, SELLER_UID);

        assertThat(thread.getId()).isNotNull();
        assertThat(thread.getProductId()).isEqualTo(PRODUCT_ID);
        assertThat(thread.getBuyerId()).isEqualTo(BUYER_ID);
        assertThat(thread.getSellerUserId()).isEqualTo(SELLER_UID);
        assertThat(thread.getStatus()).isEqualTo(InquiryStatus.OPEN);
        assertThat(thread.getCreatedAt()).isEqualTo(thread.getLastActivityAt());
    }

    @Test
    void isParticipant_buyer_returnsTrue() {
        InquiryThread thread = InquiryThread.create(PRODUCT_ID, BUYER_ID, SELLER_UID);
        assertThat(thread.isParticipant(BUYER_ID)).isTrue();
    }

    @Test
    void isParticipant_seller_returnsTrue() {
        InquiryThread thread = InquiryThread.create(PRODUCT_ID, BUYER_ID, SELLER_UID);
        assertThat(thread.isParticipant(SELLER_UID)).isTrue();
    }

    @Test
    void isParticipant_stranger_returnsFalse() {
        InquiryThread thread = InquiryThread.create(PRODUCT_ID, BUYER_ID, SELLER_UID);
        assertThat(thread.isParticipant(STRANGER_ID)).isFalse();
    }

    @Test
    void close_setsStatusClosed() {
        InquiryThread thread = InquiryThread.create(PRODUCT_ID, BUYER_ID, SELLER_UID);
        thread.close();
        assertThat(thread.getStatus()).isEqualTo(InquiryStatus.CLOSED);
    }

    @Test
    void touch_updatesLastActivityAt() throws InterruptedException {
        InquiryThread thread = InquiryThread.create(PRODUCT_ID, BUYER_ID, SELLER_UID);
        Instant original = thread.getLastActivityAt();
        Thread.sleep(5);
        thread.touch();
        assertThat(thread.getLastActivityAt()).isAfter(original);
    }

    @Test
    void reconstitute_buildsThreadWithGivenState() {
        Instant created = Instant.parse("2026-01-01T00:00:00Z");
        Instant active = Instant.parse("2026-01-02T00:00:00Z");

        InquiryThread thread = InquiryThread.reconstitute(
                InquiryThreadId.of("11111111-1111-1111-1111-111111111111"),
                PRODUCT_ID, BUYER_ID, SELLER_UID, InquiryStatus.CLOSED, created, active);

        assertThat(thread.getStatus()).isEqualTo(InquiryStatus.CLOSED);
        assertThat(thread.getCreatedAt()).isEqualTo(created);
        assertThat(thread.getLastActivityAt()).isEqualTo(active);
    }
}
