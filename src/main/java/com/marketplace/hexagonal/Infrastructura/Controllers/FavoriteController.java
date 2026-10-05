package com.marketplace.hexagonal.Infrastructura.Controllers;

import com.marketplace.hexagonal.Application.Favorite.Services.FavoriteService;
import com.marketplace.hexagonal.Application.User.Services.UserService;
import com.marketplace.hexagonal.Domain.Favorite.Models.Favorite;
import com.marketplace.hexagonal.Domain.Product.Models.ProductId;
import com.marketplace.hexagonal.Domain.User.Exceptions.UserNotFoundException;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import com.marketplace.hexagonal.Infrastructura.DTO.Favorite.FavoriteResponseDTO;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/favorites")
@AllArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final UserService userService;

    @GetMapping
    public List<FavoriteResponseDTO> listFavorites(Authentication auth) {
        UserId userId = resolveUserId(auth);
        return favoriteService.listFavorites(userId).stream()
                .map(FavoriteResponseDTO::from)
                .toList();
    }

    @PostMapping("/{productId}")
    @ResponseStatus(HttpStatus.CREATED)
    public FavoriteResponseDTO addFavorite(@PathVariable String productId,
                                           Authentication auth) {
        UserId userId = resolveUserId(auth);
        Favorite saved = favoriteService.addFavorite(userId, ProductId.of(productId));
        return FavoriteResponseDTO.from(saved);
    }

    @DeleteMapping("/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeFavorite(@PathVariable String productId, Authentication auth) {
        UserId userId = resolveUserId(auth);
        favoriteService.removeFavorite(userId, ProductId.of(productId));
    }

    private UserId resolveUserId(Authentication auth) {
        return userService.findByEmail(auth.getName())
                .map(u -> u.getUserId())
                .orElseThrow(() -> new UserNotFoundException(
                        "Authenticated user not found: " + auth.getName()));
    }
}
