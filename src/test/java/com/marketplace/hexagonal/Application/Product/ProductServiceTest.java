package com.marketplace.hexagonal.Application.Product;

import com.marketplace.hexagonal.Application.Product.Services.ProductService;
import com.marketplace.hexagonal.Application.Product.UseCases.*;
import com.marketplace.hexagonal.Domain.Product.Exceptions.CategoryNotFoundException;
import com.marketplace.hexagonal.Domain.Product.Exceptions.InvalidProductStatusTransitionException;
import com.marketplace.hexagonal.Domain.Product.Exceptions.ProductNotFoundException;
import com.marketplace.hexagonal.Domain.Product.Exceptions.ProductOwnershipException;
import com.marketplace.hexagonal.Domain.Product.Exceptions.SlugAlreadyExistsException;
import com.marketplace.hexagonal.Domain.Product.Models.*;
import com.marketplace.hexagonal.Domain.Product.Ports.Out.CategoryRepositoryPort;
import com.marketplace.hexagonal.Domain.Product.Ports.Out.ProductRepositoryPort;
import com.marketplace.hexagonal.Domain.Product.Events.LowStockAlertEvent;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock ProductRepositoryPort productRepo;
    @Mock CategoryRepositoryPort categoryRepo;
    @Mock ApplicationEventPublisher eventPublisher;
    ProductService productService;

    private static final SellerProfileId SELLER_ID = SellerProfileId.of("550e8400-e29b-41d4-a716-446655440000");
    private static final SellerProfileId OTHER_SELLER = SellerProfileId.of("aa0e8400-e29b-41d4-a716-446655440099");
    private static final CategoryId CATEGORY_ID = CategoryId.of("660e8400-e29b-41d4-a716-446655440001");
    private static final Money PRICE = Money.of(new BigDecimal("50.00"), "USD");

    @BeforeEach
    void setUp() {
        productService = new ProductService(
                new CreateProductUseCaseImpl(productRepo, categoryRepo),
                new UpdateProductUseCaseImpl(productRepo, categoryRepo),
                new PublishProductUseCaseImpl(productRepo),
                new DeactivateProductUseCaseImpl(productRepo),
                new ReactivateProductUseCaseImpl(productRepo),
                new DeleteProductUseCaseImpl(productRepo),
                new GetProductUseCaseImpl(productRepo),
                new ListProductsUseCaseImpl(productRepo),
                new SearchProductsUseCaseImpl(productRepo),
                new AddProductVariantUseCaseImpl(productRepo),
                new UpdateProductVariantUseCaseImpl(productRepo),
                new RemoveProductVariantUseCaseImpl(productRepo),
                new AddProductImageUseCaseImpl(productRepo),
                new RemoveProductImageUseCaseImpl(productRepo),
                new SetPrimaryImageUseCaseImpl(productRepo),
                eventPublisher
        );
    }

    @Test
    void createProduct_whenValidInput_shouldSaveAndReturnDraftProduct() {
        when(categoryRepo.findById(CATEGORY_ID)).thenReturn(Optional.of(buildCategory()));
        when(productRepo.existsBySlug("test-product")).thenReturn(false);
        when(productRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Product result = productService.createProduct(
                SELLER_ID, CATEGORY_ID, "Test", "desc", "test-product", PRICE, ProductType.PHYSICAL);

        assertThat(result.getStatus()).isEqualTo(ProductStatus.DRAFT);
        assertThat(result.getProductType()).isEqualTo(ProductType.PHYSICAL);
        assertThat(result.getSellerId()).isEqualTo(SELLER_ID);
        verify(productRepo).save(any());
    }

    @Test
    void createProduct_whenCategoryNotFound_shouldThrow() {
        when(categoryRepo.findById(CATEGORY_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.createProduct(
                SELLER_ID, CATEGORY_ID, "Test", "desc", "test-product", PRICE, ProductType.PHYSICAL))
                .isInstanceOf(CategoryNotFoundException.class);
    }

    @Test
    void createProduct_whenSlugAlreadyExists_shouldThrow() {
        when(categoryRepo.findById(CATEGORY_ID)).thenReturn(Optional.of(buildCategory()));
        when(productRepo.existsBySlug("test-product")).thenReturn(true);

        assertThatThrownBy(() -> productService.createProduct(
                SELLER_ID, CATEGORY_ID, "Test", "desc", "test-product", PRICE, ProductType.PHYSICAL))
                .isInstanceOf(SlugAlreadyExistsException.class);
    }

    @Test
    void updateProduct_whenOwner_shouldUpdateAndSave() {
        Product existing = buildDraftProduct(ProductType.PHYSICAL);
        when(productRepo.findById(existing.getId())).thenReturn(Optional.of(existing));
        when(categoryRepo.findById(CATEGORY_ID)).thenReturn(Optional.of(buildCategory()));
        when(productRepo.existsBySlug("new-slug")).thenReturn(false);
        when(productRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Product result = productService.updateProduct(
                existing.getId(), SELLER_ID, CATEGORY_ID,
                "Updated", "new desc", "new-slug", PRICE, ProductType.DIGITAL);

        assertThat(result.getName()).isEqualTo("Updated");
        assertThat(result.getProductType()).isEqualTo(ProductType.DIGITAL);
        verify(productRepo).save(any());
    }

    @Test
    void updateProduct_whenNotOwner_shouldThrow() {
        Product existing = buildDraftProduct(ProductType.PHYSICAL);
        when(productRepo.findById(existing.getId())).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> productService.updateProduct(
                existing.getId(), OTHER_SELLER, CATEGORY_ID,
                "name", "desc", "slug", PRICE, ProductType.PHYSICAL))
                .isInstanceOf(ProductOwnershipException.class);
    }

    @Test
    void publishProduct_whenDigital_shouldPublish() {
        Product product = buildDraftProduct(ProductType.DIGITAL);
        when(productRepo.findById(product.getId())).thenReturn(Optional.of(product));
        when(productRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Product result = productService.publishProduct(product.getId(), SELLER_ID);

        assertThat(result.getStatus()).isEqualTo(ProductStatus.ACTIVE);
    }

    @Test
    void publishProduct_whenPhysicalWithImage_shouldPublish() {
        Product product = buildDraftProduct(ProductType.PHYSICAL);
        product.addImage("http://img.example.com/1.jpg", "alt");
        when(productRepo.findById(product.getId())).thenReturn(Optional.of(product));
        when(productRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Product result = productService.publishProduct(product.getId(), SELLER_ID);

        assertThat(result.getStatus()).isEqualTo(ProductStatus.ACTIVE);
    }

    @Test
    void publishProduct_whenPhysicalWithoutImage_shouldThrow() {
        Product product = buildDraftProduct(ProductType.PHYSICAL);
        when(productRepo.findById(product.getId())).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> productService.publishProduct(product.getId(), SELLER_ID))
                .isInstanceOf(InvalidProductStatusTransitionException.class)
                .hasMessageContaining("at least one image");
    }

    @Test
    void publishProduct_whenNotOwner_shouldThrow() {
        Product product = buildDraftProduct(ProductType.DIGITAL);
        when(productRepo.findById(product.getId())).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> productService.publishProduct(product.getId(), OTHER_SELLER))
                .isInstanceOf(ProductOwnershipException.class);
    }

    @Test
    void publishProduct_whenNotFound_shouldThrow() {
        ProductId unknownId = ProductId.of("00000000-0000-0000-0000-000000000000");
        when(productRepo.findById(unknownId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.publishProduct(unknownId, SELLER_ID))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void deactivateProduct_whenActive_shouldSetInactive() {
        Product product = buildDraftProduct(ProductType.DIGITAL);
        product.publish();
        when(productRepo.findById(product.getId())).thenReturn(Optional.of(product));
        when(productRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Product result = productService.deactivateProduct(product.getId(), SELLER_ID);

        assertThat(result.getStatus()).isEqualTo(ProductStatus.INACTIVE);
    }

    @Test
    void reactivateProduct_whenInactive_shouldSetActive() {
        Product product = buildDraftProduct(ProductType.DIGITAL);
        product.publish();
        product.deactivate();
        when(productRepo.findById(product.getId())).thenReturn(Optional.of(product));
        when(productRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Product result = productService.reactivateProduct(product.getId(), SELLER_ID);

        assertThat(result.getStatus()).isEqualTo(ProductStatus.ACTIVE);
    }

    @Test
    void deleteProduct_shouldSoftDelete() {
        Product product = buildDraftProduct(ProductType.PHYSICAL);
        when(productRepo.findById(product.getId())).thenReturn(Optional.of(product));
        when(productRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        productService.deleteProduct(product.getId(), SELLER_ID);

        verify(productRepo).save(argThat(p -> p.getStatus() == ProductStatus.DELETED));
    }

    @Test
    void getProductById_whenFound_shouldReturn() {
        Product product = buildDraftProduct(ProductType.PHYSICAL);
        when(productRepo.findById(product.getId())).thenReturn(Optional.of(product));

        Product result = productService.getProductById(product.getId());

        assertThat(result.getId()).isEqualTo(product.getId());
    }

    @Test
    void getProductById_whenNotFound_shouldThrow() {
        ProductId unknownId = ProductId.of("00000000-0000-0000-0000-000000000000");
        when(productRepo.findById(unknownId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProductById(unknownId))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void getAllActiveProducts_shouldReturnActiveList() {
        Product active = buildDraftProduct(ProductType.DIGITAL);
        active.publish();
        when(productRepo.findAllActive()).thenReturn(List.of(active));

        List<Product> result = productService.getAllActiveProducts();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(ProductStatus.ACTIVE);
    }

    @Test
    void getProductsBySeller_shouldDelegateToRepo() {
        when(productRepo.findBySellerId(SELLER_ID)).thenReturn(List.of(buildDraftProduct(ProductType.PHYSICAL)));

        List<Product> result = productService.getProductsBySeller(SELLER_ID);

        assertThat(result).hasSize(1);
        verify(productRepo).findBySellerId(SELLER_ID);
    }

    @Test
    void addVariant_shouldAddAndSave() {
        Product product = buildDraftProduct(ProductType.PHYSICAL);
        when(productRepo.findById(product.getId())).thenReturn(Optional.of(product));
        when(productRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Product result = productService.addVariant(
                product.getId(), SELLER_ID, "SKU-001", Map.of("color", "red"), null, 10);

        assertThat(result.getVariants()).hasSize(1);
        assertThat(result.getVariants().get(0).getSku()).isEqualTo("SKU-001");
    }

    @Test
    void addImage_shouldAddAndSave() {
        Product product = buildDraftProduct(ProductType.PHYSICAL);
        when(productRepo.findById(product.getId())).thenReturn(Optional.of(product));
        when(productRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Product result = productService.addImage(
                product.getId(), SELLER_ID, "http://img.example.com/1.jpg", "alt");

        assertThat(result.getImages()).hasSize(1);
        assertThat(result.getImages().get(0).getUrl()).isEqualTo("http://img.example.com/1.jpg");
        assertThat(result.getImages().get(0).isPrimary()).isTrue();
    }

    @Test
    void removeImage_shouldRemoveAndSave() {
        Product product = buildDraftProduct(ProductType.PHYSICAL);
        product.addImage("http://img.example.com/1.jpg", "alt");
        ProductImageId imageId = product.getImages().get(0).getId();
        when(productRepo.findById(product.getId())).thenReturn(Optional.of(product));
        when(productRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Product result = productService.removeImage(product.getId(), SELLER_ID, imageId);

        assertThat(result.getImages()).isEmpty();
    }

    @Test
    void setPrimaryImage_shouldChangePrimaryAndSave() {
        Product product = buildDraftProduct(ProductType.PHYSICAL);
        product.addImage("http://img.example.com/1.jpg", "first");
        product.addImage("http://img.example.com/2.jpg", "second");
        ProductImageId secondId = product.getImages().get(1).getId();
        when(productRepo.findById(product.getId())).thenReturn(Optional.of(product));
        when(productRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Product result = productService.setPrimaryImage(product.getId(), SELLER_ID, secondId);

        assertThat(result.getImages().get(1).isPrimary()).isTrue();
        assertThat(result.getImages().get(0).isPrimary()).isFalse();
    }

    private Product buildDraftProduct(ProductType type) {
        return Product.create(SELLER_ID, CATEGORY_ID, "Test Product",
                "A test product", "test-product", PRICE, type);
    }

    @Test
    void searchProducts_shouldDelegateToRepoAndReturnPageResult() {
        Product active = buildDraftProduct(ProductType.DIGITAL);
        active.publish();
        PageResult<Product> pageResult = new PageResult<>(List.of(active), 0, 20, 1L, 1, true);
        ProductSearchCriteria criteria = new ProductSearchCriteria(null, null, null, null, null, null);
        when(productRepo.search(criteria, 0, 20)).thenReturn(pageResult);

        PageResult<Product> result = productService.searchProducts(criteria, 0, 20);

        assertThat(result.content()).hasSize(1);
        assertThat(result.totalElements()).isEqualTo(1);
        assertThat(result.page()).isEqualTo(0);
        verify(productRepo).search(criteria, 0, 20);
    }

    @Test
    void searchProducts_withKeywordAndPriceFilter_shouldPassCriteriaToRepo() {
        PageResult<Product> empty = new PageResult<>(List.of(), 0, 20, 0L, 0, true);
        ProductSearchCriteria criteria = new ProductSearchCriteria(
                "iphone", CATEGORY_ID, null,
                new BigDecimal("100.00"), new BigDecimal("500.00"), ProductType.PHYSICAL);
        when(productRepo.search(criteria, 0, 20)).thenReturn(empty);

        PageResult<Product> result = productService.searchProducts(criteria, 0, 20);

        assertThat(result.totalElements()).isEqualTo(0);
        verify(productRepo).search(criteria, 0, 20);
    }

    private Category buildCategory() {
        return Category.create("Electronics", "Electronic devices", "electronics", null);
    }

    @Test
    void updateVariant_whenStockAtThreshold_publishesLowStockAlertEvent() {
        Product product = buildDraftProduct(ProductType.PHYSICAL);
        product.addVariant("SKU-001", Map.of(), null, 20);
        ProductVariantId variantId = product.getVariants().get(0).getId();
        when(productRepo.findById(product.getId())).thenReturn(Optional.of(product));
        when(productRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        productService.updateVariant(product.getId(), SELLER_ID, variantId,
                "SKU-001", Map.of(), null, 5);

        verify(eventPublisher).publishEvent((Object) argThat(evt ->
                evt instanceof LowStockAlertEvent e
                        && e.currentStock() == 5
                        && e.sku().equals("SKU-001")
                        && e.sellerProfileId().equals(SELLER_ID)));
    }

    @Test
    void updateVariant_whenStockBelowThreshold_publishesLowStockAlertEvent() {
        Product product = buildDraftProduct(ProductType.PHYSICAL);
        product.addVariant("SKU-002", Map.of(), null, 20);
        ProductVariantId variantId = product.getVariants().get(0).getId();
        when(productRepo.findById(product.getId())).thenReturn(Optional.of(product));
        when(productRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        productService.updateVariant(product.getId(), SELLER_ID, variantId,
                "SKU-002", Map.of(), null, 2);

        verify(eventPublisher).publishEvent((Object) argThat(evt ->
                evt instanceof LowStockAlertEvent e && e.currentStock() == 2));
    }

    @Test
    void updateVariant_whenStockAboveThreshold_doesNotPublishLowStockEvent() {
        Product product = buildDraftProduct(ProductType.PHYSICAL);
        product.addVariant("SKU-003", Map.of(), null, 10);
        ProductVariantId variantId = product.getVariants().get(0).getId();
        when(productRepo.findById(product.getId())).thenReturn(Optional.of(product));
        when(productRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        productService.updateVariant(product.getId(), SELLER_ID, variantId,
                "SKU-003", Map.of(), null, 6);

        verifyNoInteractions(eventPublisher);
    }
}
