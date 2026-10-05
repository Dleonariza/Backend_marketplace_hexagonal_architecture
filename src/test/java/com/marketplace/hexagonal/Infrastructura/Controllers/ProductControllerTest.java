package com.marketplace.hexagonal.Infrastructura.Controllers;

import tools.jackson.databind.ObjectMapper;
import com.marketplace.hexagonal.Application.Product.Services.CategoryService;
import com.marketplace.hexagonal.Application.Product.Services.ProductService;
import com.marketplace.hexagonal.Application.Seller.Services.SellerService;
import com.marketplace.hexagonal.Application.User.Services.UserService;
import com.marketplace.hexagonal.Domain.Product.Exceptions.ProductNotFoundException;
import com.marketplace.hexagonal.Domain.Product.Models.*;
import com.marketplace.hexagonal.Domain.Seller.Models.*;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock ProductService productService;
    @Mock CategoryService categoryService;
    @Mock SellerService sellerService;
    @Mock UserService userService;
    @InjectMocks ProductController productController;
    MockMvc mockMvc;
    final ObjectMapper objectMapper = new ObjectMapper();

    private static final String PRODUCT_ID = "bc66d237-f54d-4bad-b521-0eba3b06304c";
    private static final String CATEGORY_ID = "660e8400-e29b-41d4-a716-446655440001";
    private static final String SELLER_ID = "770e8400-e29b-41d4-a716-446655440002";
    private static final String USER_EMAIL = "seller@test.com";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(productController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .addFilter((req, res, chain) -> {
                    chain.doFilter(new HttpServletRequestWrapper((HttpServletRequest) req) {
                        @Override
                        public java.security.Principal getUserPrincipal() {
                            Authentication a = SecurityContextHolder.getContext().getAuthentication();
                            return a;
                        }
                    }, res);
                })
                .build();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getProductById_whenFound_shouldReturn200WithEnrichedDetail() throws Exception {
        Product product = buildProduct();
        Category category = buildCategory();
        SellerProfile seller = buildSeller();

        when(productService.getProductById(ProductId.of(PRODUCT_ID))).thenReturn(product);
        when(categoryService.getCategoryById(product.getCategoryId())).thenReturn(category);
        when(sellerService.getById(product.getSellerId())).thenReturn(seller);
        when(productService.searchProducts(any(), eq(0), eq(5)))
                .thenReturn(new PageResult<>(List.of(), 0, 5, 0L, 0, true));

        mockMvc.perform(get("/products/" + PRODUCT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(PRODUCT_ID))
                .andExpect(jsonPath("$.name").value("Test Product"))
                .andExpect(jsonPath("$.categoryName").value("Electronics"))
                .andExpect(jsonPath("$.categorySlug").value("electronics"))
                .andExpect(jsonPath("$.sellerStoreName").value("Mi Tienda Tech"))
                .andExpect(jsonPath("$.relatedProducts").isArray());
    }

    @Test
    void getProductById_whenNotFound_shouldReturn404() throws Exception {
        when(productService.getProductById(ProductId.of(PRODUCT_ID)))
                .thenThrow(new ProductNotFoundException(ProductId.of(PRODUCT_ID)));

        mockMvc.perform(get("/products/" + PRODUCT_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    void getProductById_withRelatedProducts_shouldReturnUpTo4() throws Exception {
        Product product = buildProduct();
        Category category = buildCategory();
        SellerProfile seller = buildSeller();

        Product other1 = buildProductWithId("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
        Product other2 = buildProductWithId("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");
        Product other3 = buildProductWithId("cccccccc-cccc-cccc-cccc-cccccccccccc");
        Product other4 = buildProductWithId("dddddddd-dddd-dddd-dddd-dddddddddddd");

        when(productService.getProductById(ProductId.of(PRODUCT_ID))).thenReturn(product);
        when(categoryService.getCategoryById(product.getCategoryId())).thenReturn(category);
        when(sellerService.getById(product.getSellerId())).thenReturn(seller);
        when(productService.searchProducts(any(), eq(0), eq(5)))
                .thenReturn(new PageResult<>(List.of(product, other1, other2, other3, other4), 0, 5, 5L, 1, true));

        mockMvc.perform(get("/products/" + PRODUCT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.relatedProducts.length()").value(4));
    }

    @Test
    void getAllActiveProducts_shouldReturn200WithList() throws Exception {
        when(productService.getAllActiveProducts()).thenReturn(List.of(buildProduct()));

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Test Product"));
    }

    @Test
    void getAllActiveProducts_whenEmpty_shouldReturnEmptyArray() throws Exception {
        when(productService.getAllActiveProducts()).thenReturn(List.of());

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void searchProducts_withNoFilters_shouldReturn200WithPagedResult() throws Exception {
        PageResult<Product> page = new PageResult<>(
                List.of(buildProduct()), 0, 20, 1L, 1, true);
        when(productService.searchProducts(any(), eq(0), eq(20))).thenReturn(page);

        mockMvc.perform(get("/products/search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].name").value("Test Product"))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.last").value(true));
    }

    @Test
    void searchProducts_withKeyword_shouldPassItToCriteria() throws Exception {
        PageResult<Product> page = new PageResult<>(List.of(), 0, 20, 0L, 0, true);
        when(productService.searchProducts(any(), eq(0), eq(20))).thenReturn(page);

        mockMvc.perform(get("/products/search").param("keyword", "iphone"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void searchProducts_withCustomPageSize_shouldRespectParams() throws Exception {
        PageResult<Product> page = new PageResult<>(List.of(), 1, 10, 0L, 0, true);
        when(productService.searchProducts(any(), eq(1), eq(10))).thenReturn(page);

        mockMvc.perform(get("/products/search")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.size").value(10));
    }

    @Test
    void searchProducts_withInvalidProductType_shouldReturn400() throws Exception {
        mockMvc.perform(get("/products/search").param("productType", "INVALID"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getMyProducts_whenAuthenticated_shouldReturn200() throws Exception {
        setSellerAuth();
        when(userService.findByEmail(USER_EMAIL))
                .thenReturn(Optional.of(buildUserModel()));
        when(sellerService.findByUserId(any()))
                .thenReturn(Optional.of(buildSeller()));
        when(productService.getProductsBySeller(any()))
                .thenReturn(List.of(buildProduct()));

        mockMvc.perform(get("/products/seller/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Product"));
    }

    private Product buildProduct() {
        return Product.reconstitute(
                ProductId.of(PRODUCT_ID),
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

    private Product buildProductWithId(String id) {
        return Product.reconstitute(
                ProductId.of(id),
                SellerProfileId.of(SELLER_ID),
                CategoryId.of(CATEGORY_ID),
                "Related Product", "desc", "related-" + id.substring(0, 8),
                Money.of(new BigDecimal("49.99"), "USD"),
                ProductType.PHYSICAL,
                ProductStatus.ACTIVE,
                List.of(),
                List.of(),
                Instant.now(),
                Instant.now()
        );
    }

    private Category buildCategory() {
        return Category.reconstitute(
                CategoryId.of(CATEGORY_ID),
                "Electronics", "Electronic devices", "electronics",
                null, true, Instant.now(), Instant.now()
        );
    }

    private SellerProfile buildSeller() {
        return SellerProfile.reconstitute(
                SellerProfileId.of(SELLER_ID),
                UserId.of("550e8400-e29b-41d4-a716-446655440000"),
                "Mi Tienda Tech", "Tienda de tecnología", "Electronics",
                "contacto@tienda.com", "3001234567", null,
                new IdentityDocument(DocumentType.CC, "12345678"),
                new BankingInfo("Bancolombia", "12345678901", AccountType.SAVINGS, "John Doe", "12345678"),
                SellerStatus.ACTIVE, null, null,
                Instant.now(), null, null
        );
    }

    private com.marketplace.hexagonal.Domain.User.Models.UserModel buildUserModel() {
        return com.marketplace.hexagonal.Domain.User.Models.UserModel.create(
                USER_EMAIL, "seller1", "hash", "Seller", null, "3001234567", List.of());
    }

    private void setSellerAuth() {
        var auth = new UsernamePasswordAuthenticationToken(
                USER_EMAIL, null,
                List.of(new SimpleGrantedAuthority("ROLE_SELLER")));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
