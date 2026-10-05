package com.marketplace.hexagonal.Infrastructura.Controllers;

import com.marketplace.hexagonal.Application.Product.Services.ProductService;
import com.marketplace.hexagonal.Application.Seller.Services.SellerService;
import com.marketplace.hexagonal.Domain.Product.Models.*;
import com.marketplace.hexagonal.Domain.Seller.Exceptions.SellerProfileNotFoundException;
import com.marketplace.hexagonal.Domain.Seller.Models.*;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import com.marketplace.hexagonal.Infrastructura.Exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class StoreControllerTest {

    @Mock SellerService sellerService;
    @Mock ProductService productService;
    @InjectMocks StoreController storeController;
    MockMvc mockMvc;

    private static final String SELLER_ID = "770e8400-e29b-41d4-a716-446655440002";
    private static final String CATEGORY_ID = "660e8400-e29b-41d4-a716-446655440001";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(storeController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getStore_whenActiveSellerWithProducts_returns200WithStoreData() throws Exception {
        SellerProfile seller = buildSeller(SellerStatus.ACTIVE);
        PageResult<Product> products = new PageResult<>(
                List.of(buildProduct()), 0, 20, 1L, 1, true);

        when(sellerService.getById(SellerProfileId.of(SELLER_ID))).thenReturn(seller);
        when(productService.searchProducts(any(), eq(0), eq(20))).thenReturn(products);

        mockMvc.perform(get("/stores/" + SELLER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sellerId").value(SELLER_ID))
                .andExpect(jsonPath("$.storeName").value("Mi Tienda Tech"))
                .andExpect(jsonPath("$.storeDescription").value("Tienda de tecnología"))
                .andExpect(jsonPath("$.mainCategory").value("Electronics"))
                .andExpect(jsonPath("$.contactEmail").value("contacto@tienda.com"))
                .andExpect(jsonPath("$.products.content").isArray())
                .andExpect(jsonPath("$.products.totalElements").value(1))
                .andExpect(jsonPath("$.products.content[0].name").value("Test Product"));
    }

    @Test
    void getStore_whenActiveSellerWithNoProducts_returns200WithEmptyProducts() throws Exception {
        SellerProfile seller = buildSeller(SellerStatus.ACTIVE);
        PageResult<Product> empty = new PageResult<>(List.of(), 0, 20, 0L, 0, true);

        when(sellerService.getById(SellerProfileId.of(SELLER_ID))).thenReturn(seller);
        when(productService.searchProducts(any(), eq(0), eq(20))).thenReturn(empty);

        mockMvc.perform(get("/stores/" + SELLER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.products.content").isEmpty())
                .andExpect(jsonPath("$.products.totalElements").value(0));
    }

    @Test
    void getStore_whenSellerNotFound_shouldReturn404() throws Exception {
        when(sellerService.getById(SellerProfileId.of(SELLER_ID)))
                .thenThrow(new SellerProfileNotFoundException("Not found"));

        mockMvc.perform(get("/stores/" + SELLER_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    void getStore_whenSellerSuspended_shouldReturn404() throws Exception {
        when(sellerService.getById(SellerProfileId.of(SELLER_ID)))
                .thenReturn(buildSeller(SellerStatus.SUSPENDED));

        mockMvc.perform(get("/stores/" + SELLER_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    void getStore_whenSellerBanned_shouldReturn404() throws Exception {
        when(sellerService.getById(SellerProfileId.of(SELLER_ID)))
                .thenReturn(buildSeller(SellerStatus.BANNED));

        mockMvc.perform(get("/stores/" + SELLER_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    void getStore_whenSellerPending_shouldReturn404() throws Exception {
        when(sellerService.getById(SellerProfileId.of(SELLER_ID)))
                .thenReturn(buildSeller(SellerStatus.PENDING_APPROVAL));

        mockMvc.perform(get("/stores/" + SELLER_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    void getStore_withCustomPagination_shouldPassParamsToSearch() throws Exception {
        SellerProfile seller = buildSeller(SellerStatus.ACTIVE);
        PageResult<Product> page = new PageResult<>(List.of(), 1, 10, 0L, 0, true);

        when(sellerService.getById(SellerProfileId.of(SELLER_ID))).thenReturn(seller);
        when(productService.searchProducts(any(), eq(1), eq(10))).thenReturn(page);

        mockMvc.perform(get("/stores/" + SELLER_ID)
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.products.page").value(1))
                .andExpect(jsonPath("$.products.size").value(10));
    }

    private SellerProfile buildSeller(SellerStatus status) {
        return SellerProfile.reconstitute(
                SellerProfileId.of(SELLER_ID),
                UserId.of("550e8400-e29b-41d4-a716-446655440000"),
                "Mi Tienda Tech", "Tienda de tecnología", "Electronics",
                "contacto@tienda.com", "3001234567", null,
                new IdentityDocument(DocumentType.CC, "12345678"),
                new BankingInfo("Bancolombia", "12345678901", AccountType.SAVINGS, "John Doe", "12345678"),
                status, null, null,
                Instant.now(), null, null
        );
    }

    private Product buildProduct() {
        return Product.reconstitute(
                ProductId.of("bc66d237-f54d-4bad-b521-0eba3b06304c"),
                SellerProfileId.of(SELLER_ID),
                CategoryId.of(CATEGORY_ID),
                "Test Product", "A test description", "test-product",
                Money.of(new BigDecimal("99.99"), "USD"),
                ProductType.PHYSICAL,
                ProductStatus.ACTIVE,
                List.of(),
                List.of(),
                Instant.now(),
                Instant.now()
        );
    }
}
