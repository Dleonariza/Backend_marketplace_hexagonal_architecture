package com.marketplace.hexagonal.Infrastructura.Controllers;

import tools.jackson.databind.ObjectMapper;
import com.marketplace.hexagonal.Application.User.Services.UserService;
import com.marketplace.hexagonal.Domain.User.Exceptions.UserNotFoundException;
import com.marketplace.hexagonal.Domain.User.Models.UserModel;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import com.marketplace.hexagonal.Infrastructura.DTO.UpdateProfileRequestDTO;
import com.marketplace.hexagonal.Infrastructura.DTO.UserResponseDTO;
import com.marketplace.hexagonal.Infrastructura.Exception.GlobalExceptionHandler;
import com.marketplace.hexagonal.Infrastructura.Mappers.UserMapper;
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
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock UserService userService;
    @Mock UserMapper userMapper;
    @InjectMocks UserController userController;
    MockMvc mockMvc;
    final ObjectMapper objectMapper = new ObjectMapper();

    private static final String USER_EMAIL = "user@test.com";
    private static final String USER_ID = "550e8400-e29b-41d4-a716-446655440000";

    @BeforeEach
    void setUp() {
        var auth = new UsernamePasswordAuthenticationToken(
                USER_EMAIL, null,
                List.of(new SimpleGrantedAuthority("ROLE_BUYER")));
        SecurityContextHolder.getContext().setAuthentication(auth);

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
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
    void getProfile_whenUserExists_shouldReturn200WithProfile() throws Exception {
        UserModel user = buildUser();
        UserResponseDTO response = buildResponse();

        when(userService.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenReturn(response);

        mockMvc.perform(get("/user/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(USER_EMAIL))
                .andExpect(jsonPath("$.name").value("Test User"));
    }

    @Test
    void getProfile_whenUserNotFound_shouldReturn404() throws Exception {
        when(userService.findByEmail(USER_EMAIL))
                .thenThrow(new UserNotFoundException("Authenticated user not found."));

        mockMvc.perform(get("/user/profile"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateProfile_whenValidRequest_shouldReturn200() throws Exception {
        UpdateProfileRequestDTO request = new UpdateProfileRequestDTO("New Name", "3001234567", null);
        UserModel user = buildUser();
        UserModel updated = buildUser();
        UserResponseDTO response = buildResponse();

        when(userService.findByEmail(USER_EMAIL)).thenReturn(Optional.of(user));
        when(userService.updateProfile(any(UserId.class), anyString(), anyString(), any()))
                .thenReturn(updated);
        when(userMapper.toResponse(updated)).thenReturn(response);

        mockMvc.perform(put("/user/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(USER_EMAIL));
    }

    @Test
    void updateProfile_whenNameTooShort_shouldReturn400() throws Exception {
        UpdateProfileRequestDTO request = new UpdateProfileRequestDTO("A", null, null);

        mockMvc.perform(put("/user/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteUser_whenCalled_shouldReturn204() throws Exception {
        mockMvc.perform(delete("/user/" + USER_ID))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteUser_whenUserNotFound_shouldReturn404() throws Exception {
        org.mockito.Mockito.doThrow(new UserNotFoundException(UserId.of(USER_ID)))
                .when(userService).delete(USER_ID);

        mockMvc.perform(delete("/user/" + USER_ID))
                .andExpect(status().isNotFound());
    }

    private UserModel buildUser() {
        return UserModel.create(USER_EMAIL, "user1", "hashed", "Test User", null, "3001234567", List.of());
    }

    private UserResponseDTO buildResponse() {
        return UserResponseDTO.builder()
                .userId(USER_ID)
                .email(USER_EMAIL)
                .userName("user1")
                .name("Test User")
                .phone("3001234567")
                .status("ACTIVE")
                .roles(Set.of("ROLE_BUYER"))
                .build();
    }
}
