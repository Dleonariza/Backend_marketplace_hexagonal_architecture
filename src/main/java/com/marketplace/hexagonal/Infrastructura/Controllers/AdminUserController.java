package com.marketplace.hexagonal.Infrastructura.Controllers;

import com.marketplace.hexagonal.Application.User.Services.UserService;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import com.marketplace.hexagonal.Infrastructura.DTO.User.AdminUserDetailDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<AdminUserDetailDTO>> listUsers(
            @RequestParam(required = false) String search) {
        List<AdminUserDetailDTO> users = userService.search(search).stream()
                .map(AdminUserDetailDTO::from)
                .toList();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminUserDetailDTO> getUser(@PathVariable String id) {
        return ResponseEntity.ok(
                AdminUserDetailDTO.from(userService.findById(UserId.of(id))));
    }

    @PatchMapping("/{id}/block")
    public ResponseEntity<AdminUserDetailDTO> blockUser(@PathVariable String id) {
        return ResponseEntity.ok(
                AdminUserDetailDTO.from(userService.block(UserId.of(id))));
    }

    @PatchMapping("/{id}/unblock")
    public ResponseEntity<AdminUserDetailDTO> unblockUser(@PathVariable String id) {
        return ResponseEntity.ok(
                AdminUserDetailDTO.from(userService.unblock(UserId.of(id))));
    }
}
