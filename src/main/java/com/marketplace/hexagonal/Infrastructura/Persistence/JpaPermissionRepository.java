package com.marketplace.hexagonal.Infrastructura.Persistence;

import com.marketplace.hexagonal.Domain.User.Models.RoleAndPermission.PermissionEnum;
import com.marketplace.hexagonal.Infrastructura.Entities.User.RoleAndPermission.PermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaPermissionRepository extends JpaRepository<PermissionEntity, UUID> {
    Optional<PermissionEntity> findByName(PermissionEnum name);
}
