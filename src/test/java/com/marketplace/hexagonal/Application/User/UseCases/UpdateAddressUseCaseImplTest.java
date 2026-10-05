package com.marketplace.hexagonal.Application.User.UseCases;

import com.marketplace.hexagonal.Domain.User.Exceptions.AddressNotFoundException;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateAddressUseCaseImplTest {

    @Mock
    private UserAddressRepositoryPort userAddressRepositoryPort;

    @InjectMocks
    private UpdateAddressUseCaseImpl updateAddressUseCase;

    private final UserId userId = UserId.of("550e8400-e29b-41d4-a716-446655440000");
    private final UserAddressId addressId = UserAddressId.of("550e8400-e29b-41d4-a716-446655440001");

    @Test
    void updateAddress_whenOwnershipFails_throwsAddressNotFoundException() {
        when(userAddressRepositoryPort.existsByIdAndUserId(addressId, userId)).thenReturn(false);

        assertThatThrownBy(() -> updateAddressUseCase.updateAddress(userId, addressId, anyUpdate()))
                .isInstanceOf(AddressNotFoundException.class);
        verify(userAddressRepositoryPort, never()).save(any(), any());
    }

    @Test
    void updateAddress_whenSettingDefault_shouldClearExistingDefaults() {
        UserAddress existing = buildAddress(false);
        UserAddress update = UserAddress.builder().city("Medellín").isDefault(true).build();

        when(userAddressRepositoryPort.existsByIdAndUserId(addressId, userId)).thenReturn(true);
        when(userAddressRepositoryPort.findById(addressId)).thenReturn(Optional.of(existing));
        when(userAddressRepositoryPort.save(any(), eq(userId))).thenAnswer(inv -> inv.getArgument(0));

        updateAddressUseCase.updateAddress(userId, addressId, update);

        verify(userAddressRepositoryPort).clearDefaultByUserId(userId);
    }

    @Test
    void updateAddress_shouldApplyPartialUpdate() {
        UserAddress existing = buildAddress(false);
        UserAddress update = UserAddress.builder().city("Medellín").build();

        when(userAddressRepositoryPort.existsByIdAndUserId(addressId, userId)).thenReturn(true);
        when(userAddressRepositoryPort.findById(addressId)).thenReturn(Optional.of(existing));
        when(userAddressRepositoryPort.save(any(), eq(userId))).thenAnswer(inv -> inv.getArgument(0));

        updateAddressUseCase.updateAddress(userId, addressId, update);

        ArgumentCaptor<UserAddress> captor = ArgumentCaptor.forClass(UserAddress.class);
        verify(userAddressRepositoryPort).save(captor.capture(), eq(userId));

        UserAddress saved = captor.getValue();
        assertThat(saved.getCity()).isEqualTo("Medellín");
        assertThat(saved.getStreet()).isEqualTo(existing.getStreet());
        assertThat(saved.getCountry()).isEqualTo(existing.getCountry());
    }

    private UserAddress buildAddress(boolean isDefault) {
        return UserAddress.builder()
                .id(addressId)
                .street("Calle 123")
                .city("Bogotá")
                .state("Cundinamarca")
                .postalCode("110111")
                .country("Colombia")
                .isDefault(isDefault)
                .build();
    }

    private UserAddress anyUpdate() {
        return UserAddress.builder().city("Cali").build();
    }
}
