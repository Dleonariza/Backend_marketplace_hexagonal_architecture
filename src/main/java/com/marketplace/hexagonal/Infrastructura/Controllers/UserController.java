package com.marketplace.hexagonal.Infrastructura.Controllers;

import com.marketplace.hexagonal.Application.User.Services.UserService;
import com.marketplace.hexagonal.Domain.User.Exceptions.UserNotFoundException;
import com.marketplace.hexagonal.Domain.User.Models.UserModel;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import com.marketplace.hexagonal.Infrastructura.DTO.UpdateProfileRequestDTO;
import com.marketplace.hexagonal.Infrastructura.DTO.UserRequestDTO;
import com.marketplace.hexagonal.Infrastructura.DTO.UserResponseDTO;
import com.marketplace.hexagonal.Infrastructura.Mappers.UserMapper;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping("/create")
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        UserModel user = userMapper.toDomain(userRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userMapper.toResponse(userService.createUser(user)));
    }

    @GetMapping("/profile")
    public ResponseEntity<UserResponseDTO> getProfile(Authentication authentication) {
        String email = authentication.getName();
        UserModel user = userService.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Authenticated user not found."));
        return ResponseEntity.ok(userMapper.toResponse(user));
    }

    @PutMapping("/profile")
    public ResponseEntity<UserResponseDTO> updateProfile(
            Authentication authentication,
            @Valid @RequestBody UpdateProfileRequestDTO request) {
        String email = authentication.getName();
        UserModel current = userService.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Authenticated user not found."));

        UserModel updated = userService.updateProfile(
                current.getUserId(),
                request.name(),
                request.phone(),
                request.avatarUrl());

        return ResponseEntity.ok(userMapper.toResponse(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
