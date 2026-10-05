package com.marketplace.hexagonal.Infrastructura.Config;

import com.marketplace.hexagonal.Domain.User.Models.RoleAndPermission.PermissionEnum;
import com.marketplace.hexagonal.Domain.User.Models.RoleAndPermission.RoleEnum;
import com.marketplace.hexagonal.Domain.User.Models.UserStatus;
import com.marketplace.hexagonal.Infrastructura.Entities.User.RoleAndPermission.PermissionEntity;
import com.marketplace.hexagonal.Infrastructura.Entities.User.RoleAndPermission.RoleEntity;
import com.marketplace.hexagonal.Infrastructura.Entities.User.UserEntity;
import com.marketplace.hexagonal.Infrastructura.Persistence.JpaPermissionRepository;
import com.marketplace.hexagonal.Infrastructura.Persistence.JpaRoleRepository;
import com.marketplace.hexagonal.Infrastructura.Persistence.JpaUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final JpaRoleRepository roleRepository;
    private final JpaPermissionRepository permissionRepository;
    private final JpaUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.default.email}")
    private String adminEmail;

    @Value("${admin.default.username}")
    private String adminUsername;

    @Value("${admin.default.password}")
    private String adminPassword;

    @Value("${admin.default.name}")
    private String adminName;

    @Value("${admin.default.phone}")
    private String adminPhone;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        seedPermissions();
        seedRoles();
        seedAdminUser();
    }

    private void seedPermissions() {
        for (PermissionEnum perm : PermissionEnum.values()) {
            if (permissionRepository.findByName(perm).isEmpty()) {
                log.info("Creating permission: {}", perm);
                permissionRepository.save(PermissionEntity.builder().name(perm).build());
            }
        }
    }

    private void seedRoles() {
        for (RoleEnum role : RoleEnum.values()) {
            if (roleRepository.findByRole(role).isEmpty()) {
                log.info("Creating role: {}", role);
                List<PermissionEntity> perms = getPermissionsFor(role).stream()
                        .map(perm -> permissionRepository.findByName(perm)
                                .orElseThrow(() -> new IllegalStateException("Permission not found: " + perm)))
                        .collect(Collectors.toList());

                RoleEntity entity = new RoleEntity();
                entity.setRole(role);
                entity.setPermissions(perms);
                roleRepository.save(entity);
            }
        }
    }

    private Set<PermissionEnum> getPermissionsFor(RoleEnum role) {
        return switch (role) {
            case BUYER -> Set.of(PermissionEnum.READ_PRODUCTS, PermissionEnum.READ_ORDERS, PermissionEnum.WRITE_ORDERS);
            case SELLER -> Set.of(PermissionEnum.READ_PRODUCTS, PermissionEnum.WRITE_PRODUCTS, PermissionEnum.DELETE_PRODUCTS, PermissionEnum.READ_ORDERS);
            case SUPPORT -> Set.of(PermissionEnum.READ_TICKETS, PermissionEnum.WRITE_TICKETS, PermissionEnum.READ_ORDERS);
            case ADMIN, DEVELOPER -> new HashSet<>(Arrays.asList(PermissionEnum.values()));
        };
    }

    private void seedAdminUser() {
        if (userRepository.existsByEmail(adminEmail)) {
            log.info("Default admin already exists - skipping.");
            return;
        }

        RoleEntity adminRole = roleRepository.findByRole(RoleEnum.ADMIN)
                .orElseThrow(() -> new IllegalStateException("ADMIN role not found. Roles must be seeded first."));

        UserEntity admin = UserEntity.builder()
                .email(adminEmail)
                .userName(adminUsername)
                .password(passwordEncoder.encode(adminPassword))
                .name(adminName)
                .phone(adminPhone)
                .status(UserStatus.ACTIVE)
                .roles(Set.of(adminRole))
                .enabled(true)
                .accountNoExpired(true)
                .accountNoLocked(true)
                .credentialNoExpired(true)
                .build();

        userRepository.save(admin);
        log.info("Default admin created - email: {}", adminEmail);
    }
}
