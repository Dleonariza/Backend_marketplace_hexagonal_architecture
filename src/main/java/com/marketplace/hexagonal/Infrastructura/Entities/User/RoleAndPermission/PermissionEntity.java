package com.marketplace.hexagonal.Infrastructura.Entities.User.RoleAndPermission;

import com.marketplace.hexagonal.Domain.User.Models.RoleAndPermission.PermissionEnum;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "permissions")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PermissionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "UUID DEFAULT uuid_generate_v4()")
    private UUID id;
    @Column(nullable = false, unique = true, updatable = false)
    @Enumerated(EnumType.STRING)
    private PermissionEnum name;

    public PermissionEnum getPermission() {
        return this.name;
    }
}
