package com.marketplace.hexagonal.Application.User.UseCases;

import com.marketplace.hexagonal.Domain.User.Exceptions.PaymentMethodNotFoundException;
import com.marketplace.hexagonal.Domain.User.Models.PaymentMethod;
import com.marketplace.hexagonal.Domain.User.Models.PaymentMethodId;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import com.marketplace.hexagonal.Domain.User.Ports.Out.PaymentMethodRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SetDefaultPaymentMethodUseCaseImplTest {

    @Mock
    private PaymentMethodRepositoryPort paymentMethodRepositoryPort;

    @InjectMocks
    private SetDefaultPaymentMethodUseCaseImpl setDefaultPaymentMethodUseCase;

    private static final UserId USER_ID = UserId.of("550e8400-e29b-41d4-a716-446655440000");
    private static final PaymentMethodId PM_ID = PaymentMethodId.of("660e8400-e29b-41d4-a716-446655440001");

    @Test
    void setDefault_whenOwnershipCheckFails_throwsPaymentMethodNotFoundException() {
        when(paymentMethodRepositoryPort.existsByIdAndUserId(PM_ID, USER_ID)).thenReturn(false);

        assertThatThrownBy(() -> setDefaultPaymentMethodUseCase.setDefaultPaymentMethod(USER_ID, PM_ID))
                .isInstanceOf(PaymentMethodNotFoundException.class);

        verify(paymentMethodRepositoryPort, never()).clearDefaultByUserId(any());
        verify(paymentMethodRepositoryPort, never()).save(any());
    }

    @Test
    void setDefault_shouldClearExistingDefaultsBeforeSetting() {
        PaymentMethod existing = buildCard(false);
        when(paymentMethodRepositoryPort.existsByIdAndUserId(PM_ID, USER_ID)).thenReturn(true);
        when(paymentMethodRepositoryPort.findById(PM_ID)).thenReturn(Optional.of(existing));
        when(paymentMethodRepositoryPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        setDefaultPaymentMethodUseCase.setDefaultPaymentMethod(USER_ID, PM_ID);

        var inOrder = inOrder(paymentMethodRepositoryPort);
        inOrder.verify(paymentMethodRepositoryPort).clearDefaultByUserId(USER_ID);
        inOrder.verify(paymentMethodRepositoryPort).save(any());
    }

    @Test
    void setDefault_shouldReturnCardWithIsDefaultTrue() {
        PaymentMethod existing = buildCard(false);
        when(paymentMethodRepositoryPort.existsByIdAndUserId(PM_ID, USER_ID)).thenReturn(true);
        when(paymentMethodRepositoryPort.findById(PM_ID)).thenReturn(Optional.of(existing));
        when(paymentMethodRepositoryPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        PaymentMethod result = setDefaultPaymentMethodUseCase.setDefaultPaymentMethod(USER_ID, PM_ID);

        assertThat(result.isDefault()).isTrue();
        ArgumentCaptor<PaymentMethod> captor = ArgumentCaptor.forClass(PaymentMethod.class);
        verify(paymentMethodRepositoryPort).save(captor.capture());
        assertThat(captor.getValue().isDefault()).isTrue();
    }

    @Test
    void setDefault_whenAlreadyDefault_shouldStillClearAndResave() {
        PaymentMethod existing = buildCard(true);
        when(paymentMethodRepositoryPort.existsByIdAndUserId(PM_ID, USER_ID)).thenReturn(true);
        when(paymentMethodRepositoryPort.findById(PM_ID)).thenReturn(Optional.of(existing));
        when(paymentMethodRepositoryPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        PaymentMethod result = setDefaultPaymentMethodUseCase.setDefaultPaymentMethod(USER_ID, PM_ID);

        assertThat(result.isDefault()).isTrue();
        verify(paymentMethodRepositoryPort).clearDefaultByUserId(USER_ID);
        verify(paymentMethodRepositoryPort).save(any());
    }

    private PaymentMethod buildCard(boolean isDefault) {
        return PaymentMethod.builder()
                .id(PM_ID)
                .userId(USER_ID)
                .stripePaymentMethodId("pm_test_123")
                .last4("4242")
                .brand("visa")
                .expMonth(12)
                .expYear(2028)
                .cardholderName("John Doe")
                .isDefault(isDefault)
                .createdAt(Instant.now())
                .build();
    }
}
