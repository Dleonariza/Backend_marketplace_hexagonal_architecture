package com.marketplace.hexagonal.Infrastructura.Persistence;

import com.marketplace.hexagonal.Infrastructura.Entities.Auth.PasswordResetTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaPasswordResetTokenRepository extends JpaRepository<PasswordResetTokenEntity, UUID> {

    Optional<PasswordResetTokenEntity> findByEmail(String email);

    @Modifying
    @Query("DELETE FROM PasswordResetTokenEntity p WHERE p.expiresAt < :now")
    void deleteAllExpired(Instant now);
}
