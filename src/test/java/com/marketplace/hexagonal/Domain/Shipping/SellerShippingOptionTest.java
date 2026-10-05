package com.marketplace.hexagonal.Domain.Shipping;

import com.marketplace.hexagonal.Domain.Product.Models.Money;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;
import com.marketplace.hexagonal.Domain.Shipping.Models.SellerShippingOption;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

class SellerShippingOptionTest {

    private static final SellerProfileId SELLER = SellerProfileId.of("550e8400-e29b-41d4-a716-446655440000");
    private static final Money PRICE = Money.of(new BigDecimal("5.00"), "USD");

    @Test
    void create_shouldGenerateIdAndBeActiveByDefault() {
        SellerShippingOption option = SellerShippingOption.create(
                SELLER, "Standard", "3-5 days", PRICE, 3, 5);

        assertThat(option.getId()).isNotNull();
        assertThat(option.getName()).isEqualTo("Standard");
        assertThat(option.getSellerId()).isEqualTo(SELLER);
        assertThat(option.getPrice()).isEqualTo(PRICE);
        assertThat(option.getEstimatedDaysMin()).isEqualTo(3);
        assertThat(option.getEstimatedDaysMax()).isEqualTo(5);
        assertThat(option.isActive()).isTrue();
        assertThat(option.getCreatedAt()).isNotNull();
    }

    @Test
    void create_blankName_shouldThrow() {
        assertThatThrownBy(() ->
                SellerShippingOption.create(SELLER, "  ", null, PRICE, 1, 3))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void create_negativePrice_shouldThrow() {
        assertThatThrownBy(() ->
                SellerShippingOption.create(SELLER, "Standard", null,
                        Money.of(new BigDecimal("-1.00"), "USD"), 1, 3))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void create_zeroPriceAllowed() {
        assertThatNoException().isThrownBy(() ->
                SellerShippingOption.create(SELLER, "Free Shipping", null,
                        Money.of(BigDecimal.ZERO, "USD"), 5, 7));
    }

    @Test
    void create_maxDaysLessThanMin_shouldThrow() {
        assertThatThrownBy(() ->
                SellerShippingOption.create(SELLER, "Standard", null, PRICE, 5, 3))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void create_negativeDays_shouldThrow() {
        assertThatThrownBy(() ->
                SellerShippingOption.create(SELLER, "Standard", null, PRICE, -1, 3))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void create_sameDayDelivery_minEqualsMax_shouldBeAllowed() {
        assertThatNoException().isThrownBy(() ->
                SellerShippingOption.create(SELLER, "Same Day", null, PRICE, 0, 0));
    }

    @Test
    void update_shouldChangeAllFields() {
        SellerShippingOption option = buildOption();
        Money newPrice = Money.of(new BigDecimal("15.00"), "USD");

        option.update("Express", "1-2 days", newPrice, 1, 2);

        assertThat(option.getName()).isEqualTo("Express");
        assertThat(option.getPrice()).isEqualTo(newPrice);
        assertThat(option.getEstimatedDaysMin()).isEqualTo(1);
        assertThat(option.getEstimatedDaysMax()).isEqualTo(2);
    }

    @Test
    void update_blankName_shouldThrow() {
        SellerShippingOption option = buildOption();
        assertThatThrownBy(() -> option.update("", null, PRICE, 1, 3))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deactivate_shouldSetActiveFalse() {
        SellerShippingOption option = buildOption();
        option.deactivate();
        assertThat(option.isActive()).isFalse();
    }

    @Test
    void activate_shouldSetActiveTrue() {
        SellerShippingOption option = buildOption();
        option.deactivate();
        option.activate();
        assertThat(option.isActive()).isTrue();
    }

    private SellerShippingOption buildOption() {
        return SellerShippingOption.create(SELLER, "Standard", "3-5 days", PRICE, 3, 5);
    }
}
