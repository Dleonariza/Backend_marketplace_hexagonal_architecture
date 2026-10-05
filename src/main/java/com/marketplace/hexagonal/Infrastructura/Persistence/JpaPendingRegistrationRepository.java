package com.marketplace.hexagonal.Infrastructura.Persistence;

import com.marketplace.hexagonal.Infrastructura.Entities.Auth.PendingRegistrationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaPendingRegistrationRepository extends JpaRepository<PendingRegistrationEntity, UUID> {

    Optional<PendingRegistrationEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    @Modifying
    @Query("DELETE FROM PendingRegistrationEntity p WHERE p.expiresAt < :now")
    void deleteAllExpired(Instant now);
}
