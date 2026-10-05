package com.marketplace.hexagonal.Application.User.UseCases;

import com.marketplace.hexagonal.Domain.User.Models.PaymentMethod;
import com.marketplace.hexagonal.Domain.User.Models.PaymentMethodId;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import com.marketplace.hexagonal.Domain.User.Ports.Out.PaymentMethodRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListPaymentMethodsUseCaseImplTest {

    @Mock
    private PaymentMethodRepositoryPort paymentMethodRepositoryPort;

    @InjectMocks
    private ListPaymentMethodsUseCaseImpl listPaymentMethodsUseCase;

    private static final UserId USER_ID = UserId.of("550e8400-e29b-41d4-a716-446655440000");

    @Test
    void listPaymentMethods_shouldReturnAllCardsForUser() {
        List<PaymentMethod> cards = List.of(buildCard("4242", true), buildCard("5555", false));
        when(paymentMethodRepositoryPort.findAllByUserId(USER_ID)).thenReturn(cards);

        List<PaymentMethod> result = listPaymentMethodsUseCase.listPaymentMethods(USER_ID);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getLast4()).isEqualTo("4242");
        assertThat(result.get(0).isDefault()).isTrue();
        assertThat(result.get(1).getLast4()).isEqualTo("5555");
        assertThat(result.get(1).isDefault()).isFalse();
        verify(paymentMethodRepositoryPort).findAllByUserId(USER_ID);
    }

    @Test
    void listPaymentMethods_whenNoCards_shouldReturnEmptyList() {
        when(paymentMethodRepositoryPort.findAllByUserId(USER_ID)).thenReturn(List.of());

        List<PaymentMethod> result = listPaymentMethodsUseCase.listPaymentMethods(USER_ID);

        assertThat(result).isEmpty();
    }

    private PaymentMethod buildCard(String last4, boolean isDefault) {
        return PaymentMethod.builder()
                .id(PaymentMethodId.generate())
                .userId(USER_ID)
                .last4(last4)
                .brand("visa")
                .expMonth(12)
                .expYear(2028)
                .cardholderName("John Doe")
                .isDefault(isDefault)
                .createdAt(Instant.now())
                .build();
    }
}
