package com.marketplace.hexagonal.Infrastructura.Controllers;

import com.marketplace.hexagonal.Application.Auth.Services.RegistrationService;
import com.marketplace.hexagonal.Domain.User.Models.UserModel;
import com.marketplace.hexagonal.Infrastructura.DTO.Auth.PreRegisterResponseDTO;
import com.marketplace.hexagonal.Infrastructura.DTO.Auth.ResendCodeRequestDTO;
import com.marketplace.hexagonal.Infrastructura.DTO.Auth.VerifyEmailRequestDTO;
import com.marketplace.hexagonal.Infrastructura.DTO.UserRequestDTO;
import com.marketplace.hexagonal.Infrastructura.DTO.UserResponseDTO;
import com.marketplace.hexagonal.Infrastructura.Mappers.UserMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;
    private final UserMapper userMapper;

    @PostMapping("/register")
    public ResponseEntity<PreRegisterResponseDTO> preRegister(
            @Valid @RequestBody UserRequestDTO request) {
        String email = registrationService.preRegister(
                request.getEmail(), request.getUserName(),
                request.getPassword(), request.getName(), request.getPhone());
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(PreRegisterResponseDTO.of(email));
    }

    @PostMapping("/verify-email")
    public ResponseEntity<UserResponseDTO> verifyEmail(
            @Valid @RequestBody VerifyEmailRequestDTO request) {
        UserModel user = registrationService.verifyEmail(request.email(), request.code());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userMapper.toResponse(user));
    }

    @PostMapping("/resend-code")
    public ResponseEntity<PreRegisterResponseDTO> resendCode(
            @Valid @RequestBody ResendCodeRequestDTO request) {
        String email = registrationService.resendCode(request.email());
        return ResponseEntity.ok(PreRegisterResponseDTO.of(email));
    }
}
