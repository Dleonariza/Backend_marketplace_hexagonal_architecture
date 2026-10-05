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
class SetDefaultAddressUseCaseImplTest {

    @Mock
    private UserAddressRepositoryPort userAddressRepositoryPort;

    @InjectMocks
    private SetDefaultAddressUseCaseImpl setDefaultAddressUseCase;

    private final UserId userId = UserId.of("550e8400-e29b-41d4-a716-446655440000");
    private final UserAddressId addressId = UserAddressId.of("550e8400-e29b-41d4-a716-446655440001");

    @Test
    void setDefault_whenOwnershipFails_shouldThrowAddressNotFoundException() {
        when(userAddressRepositoryPort.existsByIdAndUserId(addressId, userId)).thenReturn(false);

        assertThatThrownBy(() -> setDefaultAddressUseCase.setDefault(userId, addressId))
                .isInstanceOf(AddressNotFoundException.class);
    }

    @Test
    void setDefault_shouldClearExistingDefaultsAndSetNew() {
        UserAddress existing = UserAddress.builder()
                .id(addressId)
                .street("Calle 45")
                .city("Cali")
                .state("Valle")
                .postalCode("760001")
                .country("Colombia")
                .isDefault(false)
                .build();

        when(userAddressRepositoryPort.existsByIdAndUserId(addressId, userId)).thenReturn(true);
        when(userAddressRepositoryPort.findById(addressId)).thenReturn(Optional.of(existing));
        when(userAddressRepositoryPort.save(any(), eq(userId))).thenAnswer(inv -> inv.getArgument(0));

        UserAddress result = setDefaultAddressUseCase.setDefault(userId, addressId);

        verify(userAddressRepositoryPort).clearDefaultByUserId(userId);
        ArgumentCaptor<UserAddress> captor = ArgumentCaptor.forClass(UserAddress.class);
        verify(userAddressRepositoryPort).save(captor.capture(), eq(userId));
        assertThat(captor.getValue().getIsDefault()).isTrue();
        assertThat(result.getIsDefault()).isTrue();
    }
}
