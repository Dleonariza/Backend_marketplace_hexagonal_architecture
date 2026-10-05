package com.marketplace.hexagonal.Application.User.UseCases;

import com.marketplace.hexagonal.Domain.User.Models.UserAddress;
import com.marketplace.hexagonal.Domain.User.Models.UserAddressId;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import com.marketplace.hexagonal.Domain.User.Ports.Out.UserAddressRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddAddressUseCaseImplTest {

    @Mock
    private UserAddressRepositoryPort userAddressRepositoryPort;

    @InjectMocks
    private AddAddressUseCaseImpl addAddressUseCase;

    private final UserId userId = UserId.of("550e8400-e29b-41d4-a716-446655440000");

    @Test
    void addAddress_whenIsDefault_shouldClearExistingDefaultsFirst() {
        UserAddress request = buildRequest(true);
        UserAddress saved = buildSaved(true);

        when(userAddressRepositoryPort.findAllByUserId(userId))
                .thenReturn(List.of(buildSaved(true)));
        when(userAddressRepositoryPort.save(any(), eq(userId))).thenReturn(saved);

        addAddressUseCase.addAddress(userId, request);

        verify(userAddressRepositoryPort).clearDefaultByUserId(userId);
        verify(userAddressRepositoryPort).save(any(), eq(userId));
    }

    @Test
    void addAddress_whenFirstAddress_shouldForceIsDefaultTrue() {
        UserAddress request = buildRequest(false);
        when(userAddressRepositoryPort.findAllByUserId(userId)).thenReturn(List.of());
        when(userAddressRepositoryPort.save(any(), eq(userId))).thenAnswer(inv -> inv.getArgument(0));

        addAddressUseCase.addAddress(userId, request);

        ArgumentCaptor<UserAddress> captor = ArgumentCaptor.forClass(UserAddress.class);
        verify(userAddressRepositoryPort).save(captor.capture(), eq(userId));
        assertThat(captor.getValue().getIsDefault()).isTrue();
    }

    @Test
    void addAddress_whenNotFirstAndNotDefault_shouldNotSetAsDefault() {
        UserAddress request = buildRequest(false);
        when(userAddressRepositoryPort.findAllByUserId(userId))
                .thenReturn(List.of(buildSaved(true)));
        when(userAddressRepositoryPort.save(any(), eq(userId))).thenAnswer(inv -> inv.getArgument(0));

        addAddressUseCase.addAddress(userId, request);

        ArgumentCaptor<UserAddress> captor = ArgumentCaptor.forClass(UserAddress.class);
        verify(userAddressRepositoryPort).save(captor.capture(), eq(userId));
        assertThat(captor.getValue().getIsDefault()).isFalse();
        verify(userAddressRepositoryPort, never()).clearDefaultByUserId(any());
    }

    private UserAddress buildRequest(boolean isDefault) {
        return UserAddress.builder()
                .street("Calle 123")
                .city("Bogotá")
                .state("Cundinamarca")
                .postalCode("110111")
                .country("Colombia")
                .isDefault(isDefault)
                .build();
    }

    private UserAddress buildSaved(boolean isDefault) {
        return UserAddress.builder()
                .id(UserAddressId.of("550e8400-e29b-41d4-a716-446655440001"))
                .street("Calle 123")
                .city("Bogotá")
                .state("Cundinamarca")
                .postalCode("110111")
                .country("Colombia")
                .isDefault(isDefault)
                .build();
    }
}
