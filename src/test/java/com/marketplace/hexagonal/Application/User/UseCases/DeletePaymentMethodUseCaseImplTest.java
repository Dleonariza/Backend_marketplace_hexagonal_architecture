package com.marketplace.hexagonal.Application.User.UseCases;

import com.marketplace.hexagonal.Domain.User.Exceptions.PaymentMethodNotFoundException;
import com.marketplace.hexagonal.Domain.User.Models.PaymentMethodId;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import com.marketplace.hexagonal.Domain.User.Ports.Out.PaymentMethodRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeletePaymentMethodUseCaseImplTest {

    @Mock
    private PaymentMethodRepositoryPort paymentMethodRepositoryPort;

    @InjectMocks
    private DeletePaymentMethodUseCaseImpl deletePaymentMethodUseCase;

    private static final UserId USER_ID = UserId.of("550e8400-e29b-41d4-a716-446655440000");
    private static final PaymentMethodId PM_ID = PaymentMethodId.of("660e8400-e29b-41d4-a716-446655440001");

    @Test
    void deletePaymentMethod_whenCardBelongsToUser_shouldCallDelete() {
        when(paymentMethodRepositoryPort.existsByIdAndUserId(PM_ID, USER_ID)).thenReturn(true);

        deletePaymentMethodUseCase.deletePaymentMethod(USER_ID, PM_ID);

        verify(paymentMethodRepositoryPort, times(1)).delete(PM_ID);
    }

    @Test
    void deletePaymentMethod_whenCardDoesNotBelongToUser_throwsAndNeverDelete() {
        when(paymentMethodRepositoryPort.existsByIdAndUserId(PM_ID, USER_ID)).thenReturn(false);

        assertThatThrownBy(() -> deletePaymentMethodUseCase.deletePaymentMethod(USER_ID, PM_ID))
                .isInstanceOf(PaymentMethodNotFoundException.class)
                .hasMessageContaining(PM_ID.getValue());

        verify(paymentMethodRepositoryPort, never()).delete(any());
    }

    @Test
    void deletePaymentMethod_whenCardNotFound_shouldThrowAndNeverDelete() {
        when(paymentMethodRepositoryPort.existsByIdAndUserId(PM_ID, USER_ID)).thenReturn(false);

        assertThatThrownBy(() -> deletePaymentMethodUseCase.deletePaymentMethod(USER_ID, PM_ID))
                .isInstanceOf(PaymentMethodNotFoundException.class);

        verify(paymentMethodRepositoryPort, never()).delete(any());
    }
}
