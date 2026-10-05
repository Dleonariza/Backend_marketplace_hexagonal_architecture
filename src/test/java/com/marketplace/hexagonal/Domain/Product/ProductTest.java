package com.marketplace.hexagonal.Domain.Product;

import com.marketplace.hexagonal.Domain.Product.Exceptions.DuplicateSkuException;
import com.marketplace.hexagonal.Domain.Product.Exceptions.InvalidProductStatusTransitionException;
import com.marketplace.hexagonal.Domain.Product.Exceptions.ProductImageNotFoundException;
import com.marketplace.hexagonal.Domain.Product.Exceptions.ProductVariantNotFoundException;
import com.marketplace.hexagonal.Domain.Product.Models.*;
import com.marketplace.hexagonal.Domain.Seller.Models.SellerProfileId;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

class ProductTest {

    private static final SellerProfileId SELLER_ID = SellerProfileId.of("550e8400-e29b-41d4-a716-446655440000");
    private static final CategoryId CATEGORY_ID = CategoryId.of("660e8400-e29b-41d4-a716-446655440001");
    private static final Money PRICE = Money.of(new BigDecimal("99.99"), "USD");

    @Test
    void create_shouldGenerateIdAndSetDraftStatus() {
        Product product = buildProduct(ProductType.PHYSICAL);

        assertThat(product.getId()).isNotNull();
        assertThat(product.getStatus()).isEqualTo(ProductStatus.DRAFT);
        assertThat(product.getProductType()).isEqualTo(ProductType.PHYSICAL);
        assertThat(product.getName()).isEqualTo("Test Product");
        assertThat(product.getVariants()).isEmpty();
        assertThat(product.getImages()).isEmpty();
        assertThat(product.getCreatedAt()).isNotNull();
        assertThat(product.getUpdatedAt()).isNotNull();
    }

    @Test
    void create_digital_shouldSetDigitalType() {
        Product product = buildProduct(ProductType.DIGITAL);

        assertThat(product.getProductType()).isEqualTo(ProductType.DIGITAL);
        assertThat(product.getStatus()).isEqualTo(ProductStatus.DRAFT);
    }

    @Test
    void publish_whenDigitalWithNoImages_shouldSucceed() {
        Product product = buildProduct(ProductType.DIGITAL);

        product.publish();

        assertThat(product.getStatus()).isEqualTo(ProductStatus.ACTIVE);
    }

    @Test
    void publish_whenPhysicalWithImage_shouldSucceed() {
        Product product = buildProduct(ProductType.PHYSICAL);
        product.addImage("http://img.example.com/photo.jpg", "alt text");

        product.publish();

        assertThat(product.getStatus()).isEqualTo(ProductStatus.ACTIVE);
    }

    @Test
    void publish_whenPhysicalWithNoImages_shouldThrow() {
        Product product = buildProduct(ProductType.PHYSICAL);

        assertThatThrownBy(product::publish)
                .isInstanceOf(InvalidProductStatusTransitionException.class)
                .hasMessageContaining("at least one image");
    }

    @Test
    void publish_whenAlreadyActive_shouldThrow() {
        Product product = buildProduct(ProductType.DIGITAL);
        product.publish();

        assertThatThrownBy(product::publish)
                .isInstanceOf(InvalidProductStatusTransitionException.class);
    }

    @Test
    void publish_whenServiceType_shouldSucceedWithoutImages() {
        Product product = buildProduct(ProductType.SERVICE);

        product.publish();

        assertThat(product.getStatus()).isEqualTo(ProductStatus.ACTIVE);
    }

    @Test
    void deactivate_whenActive_shouldSetInactive() {
        Product product = buildProduct(ProductType.DIGITAL);
        product.publish();

        product.deactivate();

        assertThat(product.getStatus()).isEqualTo(ProductStatus.INACTIVE);
    }

    @Test
    void deactivate_whenDraft_shouldThrow() {
        Product product = buildProduct(ProductType.DIGITAL);

        assertThatThrownBy(product::deactivate)
                .isInstanceOf(InvalidProductStatusTransitionException.class);
    }

    @Test
    void reactivate_whenInactive_shouldSetActive() {
        Product product = buildProduct(ProductType.DIGITAL);
        product.publish();
        product.deactivate();

        product.reactivate();

        assertThat(product.getStatus()).isEqualTo(ProductStatus.ACTIVE);
    }

    @Test
    void reactivate_whenActive_shouldThrow() {
        Product product = buildProduct(ProductType.DIGITAL);
        product.publish();

        assertThatThrownBy(product::reactivate)
                .isInstanceOf(InvalidProductStatusTransitionException.class);
    }

    @Test
    void delete_whenDraft_shouldSetDeleted() {
        Product product = buildProduct(ProductType.PHYSICAL);

        product.delete();

        assertThat(product.getStatus()).isEqualTo(ProductStatus.DELETED);
    }

    @Test
    void delete_whenActive_shouldSetDeleted() {
        Product product = buildProduct(ProductType.DIGITAL);
        product.publish();

        product.delete();

        assertThat(product.getStatus()).isEqualTo(ProductStatus.DELETED);
    }

    @Test
    void delete_whenAlreadyDeleted_shouldThrow() {
        Product product = buildProduct(ProductType.PHYSICAL);
        product.delete();

        assertThatThrownBy(product::delete)
                .isInstanceOf(InvalidProductStatusTransitionException.class);
    }

    @Test
    void updateInfo_whenNotDeleted_shouldUpdateFields() {
        Product product = buildProduct(ProductType.PHYSICAL);

        product.updateInfo(CATEGORY_ID, "Updated Name", "Updated desc", "updated-slug",
                Money.of(new BigDecimal("199.99"), "USD"), ProductType.DIGITAL);

        assertThat(product.getName()).isEqualTo("Updated Name");
        assertThat(product.getDescription()).isEqualTo("Updated desc");
        assertThat(product.getSlug()).isEqualTo("updated-slug");
        assertThat(product.getProductType()).isEqualTo(ProductType.DIGITAL);
        assertThat(product.getBasePrice().amount()).isEqualByComparingTo(new BigDecimal("199.99"));
    }

    @Test
    void updateInfo_whenDeleted_shouldThrow() {
        Product product = buildProduct(ProductType.PHYSICAL);
        product.delete();

        assertThatThrownBy(() -> product.updateInfo(
                CATEGORY_ID, "name", "desc", "slug", PRICE, ProductType.PHYSICAL))
                .isInstanceOf(InvalidProductStatusTransitionException.class);
    }

    @Test
    void addVariant_shouldAddToVariantList() {
        Product product = buildProduct(ProductType.PHYSICAL);

        ProductVariant variant = product.addVariant("SKU-001", Map.of("color", "red"), null, 10);

        assertThat(product.getVariants()).hasSize(1);
        assertThat(variant.getSku()).isEqualTo("SKU-001");
        assertThat(variant.getStock()).isEqualTo(10);
    }

    @Test
    void addVariant_whenDuplicateSku_shouldThrow() {
        Product product = buildProduct(ProductType.PHYSICAL);
        product.addVariant("SKU-001", Map.of(), null, 5);

        assertThatThrownBy(() -> product.addVariant("SKU-001", Map.of(), null, 3))
                .isInstanceOf(DuplicateSkuException.class);
    }

    @Test
    void addVariant_whenDeleted_shouldThrow() {
        Product product = buildProduct(ProductType.PHYSICAL);
        product.delete();

        assertThatThrownBy(() -> product.addVariant("SKU-001", Map.of(), null, 5))
                .isInstanceOf(InvalidProductStatusTransitionException.class);
    }

    @Test
    void updateVariant_shouldUpdateSkuAndStock() {
        Product product = buildProduct(ProductType.PHYSICAL);
        ProductVariant variant = product.addVariant("OLD-SKU", Map.of(), null, 5);

        product.updateVariant(variant.getId(), "NEW-SKU", Map.of("size", "L"), null, 20);

        assertThat(variant.getSku()).isEqualTo("NEW-SKU");
        assertThat(variant.getStock()).isEqualTo(20);
        assertThat(variant.getAttributes()).containsEntry("size", "L");
    }

    @Test
    void updateVariant_whenSkuTakenByOther_shouldThrow() {
        Product product = buildProduct(ProductType.PHYSICAL);
        product.addVariant("SKU-A", Map.of(), null, 5);
        ProductVariant v2 = product.addVariant("SKU-B", Map.of(), null, 3);

        assertThatThrownBy(() -> product.updateVariant(v2.getId(), "SKU-A", Map.of(), null, 1))
                .isInstanceOf(DuplicateSkuException.class);
    }

    @Test
    void updateVariant_whenNotFound_shouldThrow() {
        Product product = buildProduct(ProductType.PHYSICAL);

        assertThatThrownBy(() -> product.updateVariant(
                ProductVariantId.of("00000000-0000-0000-0000-000000000000"),
                "SKU", Map.of(), null, 1))
                .isInstanceOf(ProductVariantNotFoundException.class);
    }

    @Test
    void removeVariant_shouldRemoveFromList() {
        Product product = buildProduct(ProductType.PHYSICAL);
        ProductVariant variant = product.addVariant("SKU-001", Map.of(), null, 5);

        product.removeVariant(variant.getId());

        assertThat(product.getVariants()).isEmpty();
    }

    @Test
    void removeVariant_whenNotFound_shouldThrow() {
        Product product = buildProduct(ProductType.PHYSICAL);

        assertThatThrownBy(() -> product.removeVariant(
                ProductVariantId.of("00000000-0000-0000-0000-000000000000")))
                .isInstanceOf(ProductVariantNotFoundException.class);
    }

    @Test
    void addImage_firstImage_shouldBeSetAsPrimary() {
        Product product = buildProduct(ProductType.PHYSICAL);

        ProductImage image = product.addImage("http://img.example.com/1.jpg", "alt");

        assertThat(product.getImages()).hasSize(1);
        assertThat(image.isPrimary()).isTrue();
        assertThat(image.getDisplayOrder()).isEqualTo(1);
    }

    @Test
    void addImage_secondImage_shouldNotOverridePrimary() {
        Product product = buildProduct(ProductType.PHYSICAL);
        product.addImage("http://img.example.com/1.jpg", "first");

        product.addImage("http://img.example.com/2.jpg", "second");

        assertThat(product.getImages()).hasSize(2);
        assertThat(product.getImages().get(0).isPrimary()).isTrue();
        assertThat(product.getImages().get(0).getDisplayOrder()).isEqualTo(1);
        assertThat(product.getImages().get(1).isPrimary()).isFalse();
        assertThat(product.getImages().get(1).getDisplayOrder()).isEqualTo(2);
    }

    @Test
    void addImage_whenDeleted_shouldThrow() {
        Product product = buildProduct(ProductType.PHYSICAL);
        product.delete();

        assertThatThrownBy(() -> product.addImage("http://img.example.com/1.jpg", "alt"))
                .isInstanceOf(InvalidProductStatusTransitionException.class);
    }

    @Test
    void removeImage_shouldRemoveFromList() {
        Product product = buildProduct(ProductType.PHYSICAL);
        ProductImage image = product.addImage("http://img.example.com/1.jpg", "alt");

        product.removeImage(image.getId());

        assertThat(product.getImages()).isEmpty();
    }

    @Test
    void removeImage_whenPrimaryRemoved_shouldPromoteNext() {
        Product product = buildProduct(ProductType.PHYSICAL);
        ProductImage first = product.addImage("http://img.example.com/1.jpg", "first");
        ProductImage second = product.addImage("http://img.example.com/2.jpg", "second");

        product.removeImage(first.getId());

        assertThat(product.getImages()).hasSize(1);
        assertThat(second.isPrimary()).isTrue();
        assertThat(second.getDisplayOrder()).isEqualTo(1);
    }

    @Test
    void removeImage_shouldReorderDisplayOrders() {
        Product product = buildProduct(ProductType.PHYSICAL);
        ProductImage first = product.addImage("http://img.example.com/1.jpg", "first");
        ProductImage second = product.addImage("http://img.example.com/2.jpg", "second");
        ProductImage third = product.addImage("http://img.example.com/3.jpg", "third");

        product.removeImage(second.getId());

        assertThat(product.getImages()).hasSize(2);
        assertThat(first.getDisplayOrder()).isEqualTo(1);
        assertThat(third.getDisplayOrder()).isEqualTo(2);
    }

    @Test
    void removeImage_whenNotFound_shouldThrow() {
        Product product = buildProduct(ProductType.PHYSICAL);

        assertThatThrownBy(() -> product.removeImage(
                ProductImageId.of("00000000-0000-0000-0000-000000000000")))
                .isInstanceOf(ProductImageNotFoundException.class);
    }

    @Test
    void setPrimaryImage_shouldChangePrimary() {
        Product product = buildProduct(ProductType.PHYSICAL);
        ProductImage first = product.addImage("http://img.example.com/1.jpg", "first");
        ProductImage second = product.addImage("http://img.example.com/2.jpg", "second");

        product.setPrimaryImage(second.getId());

        assertThat(first.isPrimary()).isFalse();
        assertThat(second.isPrimary()).isTrue();
    }

    @Test
    void setPrimaryImage_whenNotFound_shouldThrow() {
        Product product = buildProduct(ProductType.PHYSICAL);

        assertThatThrownBy(() -> product.setPrimaryImage(
                ProductImageId.of("00000000-0000-0000-0000-000000000000")))
                .isInstanceOf(ProductImageNotFoundException.class);
    }

    private Product buildProduct(ProductType type) {
        return Product.create(SELLER_ID, CATEGORY_ID, "Test Product",
                "A test product", "test-product", PRICE, type);
    }
}
