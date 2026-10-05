package com.marketplace.hexagonal.Infrastructura.Controllers;

import tools.jackson.databind.ObjectMapper;
import com.marketplace.hexagonal.Application.User.Services.AddressService;
import com.marketplace.hexagonal.Application.User.Services.UserService;
import com.marketplace.hexagonal.Domain.User.Exceptions.AddressNotFoundException;
import com.marketplace.hexagonal.Domain.User.Models.UserAddress;
import com.marketplace.hexagonal.Domain.User.Models.UserAddressId;
import com.marketplace.hexagonal.Domain.User.Models.UserModel;
import com.marketplace.hexagonal.Infrastructura.DTO.Address.AddressRequestDTO;
import com.marketplace.hexagonal.Infrastructura.Exception.GlobalExceptionHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AddressControllerTest {

    @Mock AddressService addressService;
    @Mock UserService userService;
    @InjectMocks AddressController addressController;
    MockMvc mockMvc;
    final ObjectMapper objectMapper = new ObjectMapper();

    private static final String USER_EMAIL = "user@test.com";
    private static final String ADDRESS_ID = "660e8400-e29b-41d4-a716-446655440001";

    @BeforeEach
    void setUp() {
        var auth = new UsernamePasswordAuthenticationToken(
                USER_EMAIL, null,
                List.of(new SimpleGrantedAuthority("ROLE_BUYER")));
        SecurityContextHolder.getContext().setAuthentication(auth);

        UserModel user = UserModel.create(USER_EMAIL, "user1", "hashed", "User", null, "3001234567", List.of());
        org.mockito.Mockito.lenient()
                .when(userService.findByEmail(USER_EMAIL))
                .thenReturn(Optional.of(user));

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(addressController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .addFilter((req, res, chain) -> {
                    chain.doFilter(new HttpServletRequestWrapper((HttpServletRequest) req) {
                        @Override
                        public java.security.Principal getUserPrincipal() {
                            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                            return auth;
                        }
                    }, res);
                })
                .build();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getAddresses_shouldReturn200WithList() throws Exception {
        List<UserAddress> addresses = List.of(buildAddress(true), buildAddress(false));
        when(addressService.getAddresses(any())).thenReturn(addresses);

        mockMvc.perform(get("/user/addresses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].isDefault").value(true));
    }

    @Test
    void getAddresses_whenEmpty_shouldReturn200WithEmptyList() throws Exception {
        when(addressService.getAddresses(any())).thenReturn(List.of());

        mockMvc.perform(get("/user/addresses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void addAddress_whenValidRequest_shouldReturn201() throws Exception {
        AddressRequestDTO request = buildRequest();
        UserAddress saved = buildAddress(true);
        when(addressService.addAddress(any(), any())).thenReturn(saved);

        mockMvc.perform(post("/user/addresses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.city").value("Bogotá"))
                .andExpect(jsonPath("$.isDefault").value(true));
    }

    @Test
    void addAddress_whenMissingRequiredFields_shouldReturn400() throws Exception {
        AddressRequestDTO request = new AddressRequestDTO(null, "Bogotá", "Cundinamarca", "110111", "Colombia", false);

        mockMvc.perform(post("/user/addresses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.street").exists());
    }

    @Test
    void updateAddress_whenValidRequest_shouldReturn200() throws Exception {
        AddressRequestDTO request = buildRequest();
        UserAddress updated = buildAddress(false);
        when(addressService.updateAddress(any(), any(), any())).thenReturn(updated);

        mockMvc.perform(put("/user/addresses/" + ADDRESS_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.city").value("Bogotá"));
    }

    @Test
    void updateAddress_whenAddressNotFound_shouldReturn404() throws Exception {
        AddressRequestDTO request = buildRequest();
        when(addressService.updateAddress(any(), any(), any()))
                .thenThrow(new AddressNotFoundException(UserAddressId.of(ADDRESS_ID)));

        mockMvc.perform(put("/user/addresses/" + ADDRESS_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteAddress_whenExists_shouldReturn204() throws Exception {
        mockMvc.perform(delete("/user/addresses/" + ADDRESS_ID))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteAddress_whenNotFound_shouldReturn404() throws Exception {
        org.mockito.Mockito.doThrow(new AddressNotFoundException(UserAddressId.of(ADDRESS_ID)))
                .when(addressService).deleteAddress(any(), any());

        mockMvc.perform(delete("/user/addresses/" + ADDRESS_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    void setDefault_whenExists_shouldReturn200WithUpdatedAddress() throws Exception {
        UserAddress updated = buildAddress(true);
        when(addressService.setDefault(any(), any())).thenReturn(updated);

        mockMvc.perform(patch("/user/addresses/" + ADDRESS_ID + "/default"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isDefault").value(true));
    }

    @Test
    void setDefault_whenAddressNotFound_shouldReturn404() throws Exception {
        when(addressService.setDefault(any(), any()))
                .thenThrow(new AddressNotFoundException(UserAddressId.of(ADDRESS_ID)));

        mockMvc.perform(patch("/user/addresses/" + ADDRESS_ID + "/default"))
                .andExpect(status().isNotFound());
    }

    private UserAddress buildAddress(boolean isDefault) {
        return UserAddress.builder()
                .id(UserAddressId.of(ADDRESS_ID))
                .street("Calle 123 # 45-67")
                .city("Bogotá")
                .state("Cundinamarca")
                .postalCode("110111")
                .country("Colombia")
                .isDefault(isDefault)
                .build();
    }

    private AddressRequestDTO buildRequest() {
        return new AddressRequestDTO(
                "Calle 123 # 45-67", "Bogotá", "Cundinamarca", "110111", "Colombia", true);
    }
}
