package com.marketplace.hexagonal.Infrastructura.Controllers;

import com.marketplace.hexagonal.Application.Favorite.Services.FavoriteService;
import com.marketplace.hexagonal.Application.User.Services.UserService;
import com.marketplace.hexagonal.Domain.Favorite.Exceptions.FavoriteAlreadyExistsException;
import com.marketplace.hexagonal.Domain.Favorite.Exceptions.FavoriteNotFoundException;
import com.marketplace.hexagonal.Domain.Favorite.Models.Favorite;
import com.marketplace.hexagonal.Domain.Favorite.Models.FavoriteId;
import com.marketplace.hexagonal.Domain.Product.Exceptions.ProductNotFoundException;
import com.marketplace.hexagonal.Domain.Product.Models.Money;
import com.marketplace.hexagonal.Domain.Product.Models.Product;
import com.marketplace.hexagonal.Domain.Product.Models.ProductId;
import com.marketplace.hexagonal.Domain.User.Models.UserModel;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import com.marketplace.hexagonal.Infrastructura.Exception.GlobalExceptionHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class FavoriteControllerTest {

    @Mock FavoriteService favoriteService;
    @Mock UserService userService;
    @InjectMocks FavoriteController controller;
    MockMvc mockMvc;

    private static final String USER_EMAIL = "buyer@test.com";
    private static final UserId USER_ID = UserId.of("550e8400-e29b-41d4-a716-446655440000");
    private static final ProductId PRODUCT_ID = ProductId.of("bc66d237-f54d-4bad-b521-0eba3b06304c");

    @BeforeEach
    void setUp() {
        var auth = new UsernamePasswordAuthenticationToken(
                USER_EMAIL, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(auth);

        UserModel user = UserModel.create(USER_EMAIL, "buyer1", "hash", "Buyer", null, "300", List.of());
        org.mockito.Mockito.lenient()
                .when(userService.findByEmail(USER_EMAIL))
                .thenReturn(Optional.of(user));

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .addFilter((req, res, chain) -> {
                    chain.doFilter(new HttpServletRequestWrapper((HttpServletRequest) req) {
                        @Override public java.security.Principal getUserPrincipal() {
                            Authentication a = SecurityContextHolder.getContext().getAuthentication();
                            return a;
                        }
                    }, res);
                })
                .build();
    }

    @AfterEach
    void tearDown() { SecurityContextHolder.clearContext(); }

    @Test
    void listFavorites_returnsFavoriteList() throws Exception {
        when(favoriteService.listFavorites(any()))
                .thenReturn(List.of(buildFavorite(), buildFavorite()));

        mockMvc.perform(get("/user/favorites"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].productId").value(PRODUCT_ID.getValue()))
                .andExpect(jsonPath("$[0].productName").value("Test Product"))
                .andExpect(jsonPath("$[0].price").value(99.99));
    }

    @Test
    void listFavorites_empty_returnsEmptyArray() throws Exception {
        when(favoriteService.listFavorites(any())).thenReturn(List.of());

        mockMvc.perform(get("/user/favorites"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void addFavorite_newProduct_returns201WithFavorite() throws Exception {
        Favorite favorite = buildFavorite();
        when(favoriteService.addFavorite(any(), any())).thenReturn(favorite);

        mockMvc.perform(post("/user/favorites/" + PRODUCT_ID.getValue()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productId").value(PRODUCT_ID.getValue()))
                .andExpect(jsonPath("$.productName").value("Test Product"))
                .andExpect(jsonPath("$.productSlug").value("test-product"));
    }

    @Test
    void addFavorite_alreadyFavorited_returns409() throws Exception {
        when(favoriteService.addFavorite(any(), any()))
                .thenThrow(new FavoriteAlreadyExistsException(USER_ID, PRODUCT_ID));

        mockMvc.perform(post("/user/favorites/" + PRODUCT_ID.getValue()))
                .andExpect(status().isConflict());
    }

    @Test
    void addFavorite_productNotFound_returns404() throws Exception {
        when(favoriteService.addFavorite(any(), any()))
                .thenThrow(new ProductNotFoundException(PRODUCT_ID));

        mockMvc.perform(post("/user/favorites/" + PRODUCT_ID.getValue()))
                .andExpect(status().isNotFound());
    }

    @Test
    void removeFavorite_existingFavorite_returns204() throws Exception {
        mockMvc.perform(delete("/user/favorites/" + PRODUCT_ID.getValue()))
                .andExpect(status().isNoContent());
    }

    @Test
    void removeFavorite_notInFavorites_returns404() throws Exception {
        org.mockito.Mockito.doThrow(new FavoriteNotFoundException(PRODUCT_ID))
                .when(favoriteService).removeFavorite(any(), any());

        mockMvc.perform(delete("/user/favorites/" + PRODUCT_ID.getValue()))
                .andExpect(status().isNotFound());
    }

    private Favorite buildFavorite() {
        Product product = Product.builder()
                .id(PRODUCT_ID)
                .name("Test Product")
                .slug("test-product")
                .basePrice(Money.of(new BigDecimal("99.99"), "USD"))
                .images(List.of())
                .build();

        return Favorite.builder()
                .favoriteId(FavoriteId.generate())
                .userId(USER_ID)
                .productId(PRODUCT_ID)
                .product(product)
                .createdAt(Instant.now())
                .build();
    }
}
