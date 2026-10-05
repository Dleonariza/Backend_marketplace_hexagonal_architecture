package com.marketplace.hexagonal.Infrastructura.Persistence;

import com.marketplace.hexagonal.Infrastructura.Entities.Favorite.FavoriteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaFavoriteRepository extends JpaRepository<FavoriteEntity, UUID> {

    List<FavoriteEntity> findAllByUser_IdOrderByCreatedAtDesc(UUID userId);

    Optional<FavoriteEntity> findByUser_IdAndProduct_Id(UUID userId, UUID productId);

    boolean existsByUser_IdAndProduct_Id(UUID userId, UUID productId);

    @Modifying
    @Query("DELETE FROM FavoriteEntity f WHERE f.id = :id")
    void deleteFavoriteById(UUID id);
}
