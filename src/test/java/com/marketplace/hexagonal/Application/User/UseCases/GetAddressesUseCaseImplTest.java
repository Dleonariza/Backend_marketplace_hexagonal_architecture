package com.marketplace.hexagonal.Application.User.UseCases;

import com.marketplace.hexagonal.Domain.User.Models.UserAddress;
import com.marketplace.hexagonal.Domain.User.Models.UserAddressId;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import com.marketplace.hexagonal.Domain.User.Ports.Out.UserAddressRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAddressesUseCaseImplTest {

    @Mock
    private UserAddressRepositoryPort userAddressRepositoryPort;

    @InjectMocks
    private GetAddressesUseCaseImpl getAddressesUseCase;

    @Test
    void getAddresses_shouldReturnAllAddressesForUser() {
        UserId userId = UserId.of("550e8400-e29b-41d4-a716-446655440000");
        List<UserAddress> expected = List.of(
                buildAddress("addr-1", true),
                buildAddress("addr-2", false)
        );
        when(userAddressRepositoryPort.findAllByUserId(userId)).thenReturn(expected);

        List<UserAddress> result = getAddressesUseCase.getAddresses(userId);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getIsDefault()).isTrue();
        verify(userAddressRepositoryPort, times(1)).findAllByUserId(userId);
    }

    @Test
    void getAddresses_whenNoAddresses_shouldReturnEmptyList() {
        UserId userId = UserId.of("550e8400-e29b-41d4-a716-446655440001");
        when(userAddressRepositoryPort.findAllByUserId(userId)).thenReturn(List.of());

        List<UserAddress> result = getAddressesUseCase.getAddresses(userId);

        assertThat(result).isEmpty();
    }

    private UserAddress buildAddress(String id, boolean isDefault) {
        return UserAddress.builder()
                .id(UserAddressId.of(id.contains("-") ? id : "550e8400-e29b-41d4-a716-" + id))
                .street("Calle 123")
                .city("Bogotá")
                .state("Cundinamarca")
                .postalCode("110111")
                .country("Colombia")
                .isDefault(isDefault)
                .build();
    }
}
