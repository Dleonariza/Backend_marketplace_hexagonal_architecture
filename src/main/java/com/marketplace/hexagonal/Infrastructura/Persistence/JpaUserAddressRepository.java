package com.marketplace.hexagonal.Infrastructura.Persistence;

import com.marketplace.hexagonal.Infrastructura.Entities.User.UserAddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaUserAddressRepository extends JpaRepository<UserAddressEntity, UUID> {

    List<UserAddressEntity> findAllByUserId(UUID userId);

    boolean existsByIdAndUserId(UUID id, UUID userId);

    @Modifying
    @Query("DELETE FROM UserAddressEntity a WHERE a.id = :id")
    void deleteAddressById(UUID id);

    @Modifying
    @Query("UPDATE UserAddressEntity a SET a.isDefault = false WHERE a.user.id = :userId")
    void clearDefaultByUserId(UUID userId);
}
