package com.marketplace.hexagonal.Infrastructura.Controllers;

import com.marketplace.hexagonal.Application.Product.Services.ProductService;
import com.marketplace.hexagonal.Application.Seller.Services.SellerService;
import com.marketplace.hexagonal.Application.User.Services.UserService;
import com.marketplace.hexagonal.Domain.Product.Models.*;
import com.marketplace.hexagonal.Application.Product.Services.CategoryService;
import com.marketplace.hexagonal.Domain.Seller.Exceptions.SellerProfileNotFoundException;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfile;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;
import com.marketplace.hexagonal.Domain.User.Exceptions.UserNotFoundException;
import com.marketplace.hexagonal.Domain.User.Models.UserId;
import com.marketplace.hexagonal.Domain.User.Models.UserModel;
import com.marketplace.hexagonal.Infrastructura.DTO.Product.*;

import java.math.BigDecimal;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/products")
@AllArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final SellerService sellerService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getAllActiveProducts() {
        List<ProductResponseDTO> response = productService.getAllActiveProducts().stream()
                .map(ProductResponseDTO::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<PagedProductResponseDTO> searchProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) String sellerId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String productType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        ProductSearchCriteria criteria = new ProductSearchCriteria(
                keyword,
                categoryId != null ? CategoryId.of(categoryId) : null,
                sellerId != null ? SellerProfileId.of(sellerId) : null,
                minPrice,
                maxPrice,
                productType != null ? ProductType.valueOf(productType.toUpperCase()) : null
        );

        return ResponseEntity.ok(
                PagedProductResponseDTO.from(productService.searchProducts(criteria, page, size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDetailResponseDTO> getProductById(@PathVariable String id) {
        Product product = productService.getProductById(ProductId.of(id));

        Category category = categoryService.getCategoryById(product.getCategoryId());

        SellerProfile seller = sellerService.getById(product.getSellerId());

        ProductSearchCriteria relatedCriteria = new ProductSearchCriteria(
                null, product.getCategoryId(), null, null, null, null);
        List<Product> related = productService.searchProducts(relatedCriteria, 0, 5)
                .content()
                .stream()
                .filter(p -> !p.getId().equals(product.getId()))
                .limit(4)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ProductDetailResponseDTO.from(product, category, seller, related));
    }

    @GetMapping("/seller/me")
    public ResponseEntity<List<ProductResponseDTO>> getMyProducts(Authentication authentication) {
        SellerProfileId sellerId = resolveSellerProfileId(authentication);
        List<ProductResponseDTO> response = productService.getProductsBySeller(sellerId).stream()
                .map(ProductResponseDTO::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/seller/me/status/{status}")
    public ResponseEntity<List<ProductResponseDTO>> getMyProductsByStatus(
            Authentication authentication,
            @PathVariable String status) {
        SellerProfileId sellerId = resolveSellerProfileId(authentication);
        ProductStatus ps = ProductStatus.valueOf(status.toUpperCase());
        List<ProductResponseDTO> response = productService.getProductsBySellerAndStatus(sellerId, ps).stream()
                .map(ProductResponseDTO::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProduct(
            Authentication authentication,
            @Valid @RequestBody CreateProductRequestDTO request) {
        SellerProfileId sellerId = resolveSellerProfileId(authentication);
        Money price = Money.of(request.basePriceAmount(), request.basePriceCurrency());
        ProductType productType = ProductType.valueOf(request.productType().toUpperCase());

        Product created = productService.createProduct(
                sellerId,
                CategoryId.of(request.categoryId()),
                request.name(),
                request.description(),
                request.slug(),
                price,
                productType);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ProductResponseDTO.from(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(
            Authentication authentication,
            @PathVariable String id,
            @Valid @RequestBody UpdateProductRequestDTO request) {
        SellerProfileId sellerId = resolveSellerProfileId(authentication);
        Money price = Money.of(request.basePriceAmount(), request.basePriceCurrency());
        ProductType productType = ProductType.valueOf(request.productType().toUpperCase());

        Product updated = productService.updateProduct(
                ProductId.of(id),
                sellerId,
                CategoryId.of(request.categoryId()),
                request.name(),
                request.description(),
                request.slug(),
                price,
                productType);

        return ResponseEntity.ok(ProductResponseDTO.from(updated));
    }

    @PatchMapping("/{id}/publish")
    public ResponseEntity<ProductResponseDTO> publishProduct(
            Authentication authentication, @PathVariable String id) {
        SellerProfileId sellerId = resolveSellerProfileId(authentication);
        Product product = productService.publishProduct(ProductId.of(id), sellerId);
        return ResponseEntity.ok(ProductResponseDTO.from(product));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ProductResponseDTO> deactivateProduct(
            Authentication authentication, @PathVariable String id) {
        SellerProfileId sellerId = resolveSellerProfileId(authentication);
        Product product = productService.deactivateProduct(ProductId.of(id), sellerId);
        return ResponseEntity.ok(ProductResponseDTO.from(product));
    }

    @PatchMapping("/{id}/reactivate")
    public ResponseEntity<ProductResponseDTO> reactivateProduct(
            Authentication authentication, @PathVariable String id) {
        SellerProfileId sellerId = resolveSellerProfileId(authentication);
        Product product = productService.reactivateProduct(ProductId.of(id), sellerId);
        return ResponseEntity.ok(ProductResponseDTO.from(product));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            Authentication authentication, @PathVariable String id) {
        SellerProfileId sellerId = resolveSellerProfileId(authentication);
        productService.deleteProduct(ProductId.of(id), sellerId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/variants")
    public ResponseEntity<ProductResponseDTO> addVariant(
            Authentication authentication,
            @PathVariable String id,
            @Valid @RequestBody AddVariantRequestDTO request) {
        SellerProfileId sellerId = resolveSellerProfileId(authentication);
        Money price = (request.priceAmount() != null && request.priceCurrency() != null)
                ? Money.of(request.priceAmount(), request.priceCurrency())
                : null;

        Product product = productService.addVariant(
                ProductId.of(id), sellerId,
                request.sku(), request.attributes(), price, request.stock());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ProductResponseDTO.from(product));
    }

    @PutMapping("/{id}/variants/{variantId}")
    public ResponseEntity<ProductResponseDTO> updateVariant(
            Authentication authentication,
            @PathVariable String id,
            @PathVariable String variantId,
            @Valid @RequestBody UpdateVariantRequestDTO request) {
        SellerProfileId sellerId = resolveSellerProfileId(authentication);
        Money price = (request.priceAmount() != null && request.priceCurrency() != null)
                ? Money.of(request.priceAmount(), request.priceCurrency())
                : null;

        Product product = productService.updateVariant(
                ProductId.of(id), sellerId,
                ProductVariantId.of(variantId),
                request.sku(), request.attributes(), price, request.stock());

        return ResponseEntity.ok(ProductResponseDTO.from(product));
    }

    @DeleteMapping("/{id}/variants/{variantId}")
    public ResponseEntity<ProductResponseDTO> removeVariant(
            Authentication authentication,
            @PathVariable String id,
            @PathVariable String variantId) {
        SellerProfileId sellerId = resolveSellerProfileId(authentication);
        Product product = productService.removeVariant(
                ProductId.of(id), sellerId, ProductVariantId.of(variantId));
        return ResponseEntity.ok(ProductResponseDTO.from(product));
    }

    @PostMapping("/{id}/images")
    public ResponseEntity<ProductResponseDTO> addImage(
            Authentication authentication,
            @PathVariable String id,
            @Valid @RequestBody AddImageRequestDTO request) {
        SellerProfileId sellerId = resolveSellerProfileId(authentication);
        Product product = productService.addImage(
                ProductId.of(id), sellerId,
                request.url(), request.altText());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ProductResponseDTO.from(product));
    }

    @DeleteMapping("/{id}/images/{imageId}")
    public ResponseEntity<ProductResponseDTO> removeImage(
            Authentication authentication,
            @PathVariable String id,
            @PathVariable String imageId) {
        SellerProfileId sellerId = resolveSellerProfileId(authentication);
        Product product = productService.removeImage(
                ProductId.of(id), sellerId, ProductImageId.of(imageId));
        return ResponseEntity.ok(ProductResponseDTO.from(product));
    }

    @PatchMapping("/{id}/images/{imageId}/primary")
    public ResponseEntity<ProductResponseDTO> setPrimaryImage(
            Authentication authentication,
            @PathVariable String id,
            @PathVariable String imageId) {
        SellerProfileId sellerId = resolveSellerProfileId(authentication);
        Product product = productService.setPrimaryImage(
                ProductId.of(id), sellerId, ProductImageId.of(imageId));
        return ResponseEntity.ok(ProductResponseDTO.from(product));
    }

    private SellerProfileId resolveSellerProfileId(Authentication authentication) {
        String email = authentication.getName();
        UserId userId = userService.findByEmail(email)
                .map(UserModel::getUserId)
                .orElseThrow(() -> new UserNotFoundException("Authenticated user not found."));

        return sellerService.findByUserId(userId)
                .map(p -> p.getId())
                .orElseThrow(() -> new SellerProfileNotFoundException(
                        "No seller profile found for the current user."));
    }
}
