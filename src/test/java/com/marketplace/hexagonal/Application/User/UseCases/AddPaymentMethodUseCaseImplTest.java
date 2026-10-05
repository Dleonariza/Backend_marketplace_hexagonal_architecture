package com.marketplace.hexagonal.Application.User.UseCases;

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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddPaymentMethodUseCaseImplTest {

    @Mock
    private PaymentMethodRepositoryPort paymentMethodRepositoryPort;

    @InjectMocks
    private AddPaymentMethodUseCaseImpl addPaymentMethodUseCase;

    private static final UserId USER_ID = UserId.of("550e8400-e29b-41d4-a716-446655440000");

    @Test
    void addPaymentMethod_whenFirstCard_shouldForceIsDefaultTrue() {
        when(paymentMethodRepositoryPort.findAllByUserId(USER_ID)).thenReturn(List.of());
        when(paymentMethodRepositoryPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        PaymentMethod request = buildRequest(false);

        addPaymentMethodUseCase.addPaymentMethod(USER_ID, request);

        ArgumentCaptor<PaymentMethod> captor = ArgumentCaptor.forClass(PaymentMethod.class);
        verify(paymentMethodRepositoryPort).save(captor.capture());
        assertThat(captor.getValue().isDefault()).isTrue();
    }

    @Test
    void addPaymentMethod_whenFirstCard_shouldClearDefaultsBeforeSaving() {
        when(paymentMethodRepositoryPort.findAllByUserId(USER_ID)).thenReturn(List.of());
        when(paymentMethodRepositoryPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        addPaymentMethodUseCase.addPaymentMethod(USER_ID, buildRequest(false));

        verify(paymentMethodRepositoryPort).clearDefaultByUserId(USER_ID);
    }

    @Test
    void addPaymentMethod_whenExplicitDefault_clearExistingDefaultsFirst() {
        when(paymentMethodRepositoryPort.findAllByUserId(USER_ID))
                .thenReturn(List.of(buildSaved(true)));
        when(paymentMethodRepositoryPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        PaymentMethod request = buildRequest(true);

        addPaymentMethodUseCase.addPaymentMethod(USER_ID, request);

        verify(paymentMethodRepositoryPort).clearDefaultByUserId(USER_ID);
        ArgumentCaptor<PaymentMethod> captor = ArgumentCaptor.forClass(PaymentMethod.class);
        verify(paymentMethodRepositoryPort).save(captor.capture());
        assertThat(captor.getValue().isDefault()).isTrue();
    }

    @Test
    void addPaymentMethod_whenNotFirstAndNotDefault_shouldNotSetAsDefault() {
        when(paymentMethodRepositoryPort.findAllByUserId(USER_ID))
                .thenReturn(List.of(buildSaved(true)));
        when(paymentMethodRepositoryPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        addPaymentMethodUseCase.addPaymentMethod(USER_ID, buildRequest(false));

        ArgumentCaptor<PaymentMethod> captor = ArgumentCaptor.forClass(PaymentMethod.class);
        verify(paymentMethodRepositoryPort).save(captor.capture());
        assertThat(captor.getValue().isDefault()).isFalse();
        verify(paymentMethodRepositoryPort, never()).clearDefaultByUserId(any());
    }

    @Test
    void addPaymentMethod_shouldNormalizeBrandToLowercase() {
        when(paymentMethodRepositoryPort.findAllByUserId(USER_ID)).thenReturn(List.of());
        when(paymentMethodRepositoryPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        PaymentMethod request = buildRequest(false);
        request.setBrand("VISA");

        addPaymentMethodUseCase.addPaymentMethod(USER_ID, request);

        ArgumentCaptor<PaymentMethod> captor = ArgumentCaptor.forClass(PaymentMethod.class);
        verify(paymentMethodRepositoryPort).save(captor.capture());
        assertThat(captor.getValue().getBrand()).isEqualTo("visa");
    }

    private PaymentMethod buildRequest(boolean isDefault) {
        PaymentMethod pm = new PaymentMethod();
        pm.setStripePaymentMethodId("pm_test_123");
        pm.setLast4("4242");
        pm.setBrand("visa");
        pm.setExpMonth(12);
        pm.setExpYear(2028);
        pm.setCardholderName("John Doe");
        pm.setDefault(isDefault);
        return pm;
    }

    private PaymentMethod buildSaved(boolean isDefault) {
        return PaymentMethod.builder()
                .id(PaymentMethodId.of("660e8400-e29b-41d4-a716-446655440001"))
                .userId(USER_ID)
                .stripePaymentMethodId("pm_existing")
                .last4("1111")
                .brand("mastercard")
                .expMonth(6)
                .expYear(2027)
                .cardholderName("Jane Doe")
                .isDefault(isDefault)
                .createdAt(Instant.now())
                .build();
    }
}
