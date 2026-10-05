package com.marketplace.hexagonal.Infrastructura.Persistence;

import com.marketplace.hexagonal.Domain.User.Models.UserStatus;
import com.marketplace.hexagonal.Infrastructura.Entities.User.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaUserRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByUserName(String userName);

    @Modifying
    @Query("UPDATE UserEntity u SET u.password = :passwordHash WHERE u.email = :email")
    void updatePassword(String email, String passwordHash);

    @Query("SELECT u FROM UserEntity u WHERE " +
           "LOWER(u.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<UserEntity> searchByNameOrEmail(String keyword);

    @Query("SELECT COUNT(u) FROM UserEntity u WHERE u.createdAt >= :since")
    long countUsersSince(Instant since);

    @Query("SELECT COUNT(u) FROM UserEntity u WHERE u.status = :status")
    long countByStatus(UserStatus status);
}
