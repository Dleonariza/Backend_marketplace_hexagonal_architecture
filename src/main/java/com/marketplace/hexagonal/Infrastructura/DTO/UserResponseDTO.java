package com.marketplace.hexagonal.Infrastructura.DTO;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.Set;

@Data
@Builder
public class UserResponseDTO {
    private String userId;
    private String email;
    private String name;
    private String userName;
    private String avatarUrl;
    private String phone;
    private String status;
    private Set<String> roles;
    private Instant createdAt;
}
