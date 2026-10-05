package com.marketplace.hexagonal.Application.User.UseCases;

import com.marketplace.hexagonal.Domain.User.Exceptions.AddressNotFoundException;
import com.marketplace.hexagonal.Domain.User.Models.UserAddressId;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import com.marketplace.hexagonal.Domain.User.Ports.Out.UserAddressRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteAddressUseCaseImplTest {

    @Mock
    private UserAddressRepositoryPort userAddressRepositoryPort;

    @InjectMocks
    private DeleteAddressUseCaseImpl deleteAddressUseCase;

    private final UserId userId = UserId.of("550e8400-e29b-41d4-a716-446655440000");
    private final UserAddressId addressId = UserAddressId.of("550e8400-e29b-41d4-a716-446655440001");

    @Test
    void deleteAddress_whenAddressBelongsToUser_shouldDelete() {
        when(userAddressRepositoryPort.existsByIdAndUserId(addressId, userId)).thenReturn(true);

        deleteAddressUseCase.deleteAddress(userId, addressId);

        verify(userAddressRepositoryPort, times(1)).delete(addressId);
    }

    @Test
    void deleteAddress_whenAddressDoesNotBelongToUser_shouldThrow() {
        when(userAddressRepositoryPort.existsByIdAndUserId(addressId, userId)).thenReturn(false);

        assertThatThrownBy(() -> deleteAddressUseCase.deleteAddress(userId, addressId))
                .isInstanceOf(AddressNotFoundException.class)
                .hasMessageContaining(addressId.getValue());

        verify(userAddressRepositoryPort, never()).delete(any());
    }
}
