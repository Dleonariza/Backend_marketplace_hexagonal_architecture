package com.marketplace.hexagonal.Infrastructura.Controllers;

import com.marketplace.hexagonal.Application.Product.Services.CategoryService;
import com.marketplace.hexagonal.Domain.Product.Models.Category;
import com.marketplace.hexagonal.Domain.Product.Models.CategoryId;
import com.marketplace.hexagonal.Infrastructura.DTO.Product.CategoryResponseDTO;
import com.marketplace.hexagonal.Infrastructura.DTO.Product.CreateCategoryRequestDTO;
import com.marketplace.hexagonal.Infrastructura.DTO.Product.UpdateCategoryRequestDTO;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/categories")
@AllArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> getActiveCategories() {
        List<CategoryResponseDTO> response = categoryService.getActiveCategories().stream()
                .map(CategoryResponseDTO::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> getCategoryById(@PathVariable String id) {
        Category category = categoryService.getCategoryById(CategoryId.of(id));
        return ResponseEntity.ok(CategoryResponseDTO.from(category));
    }

    @GetMapping("/slug/{slug}")
    public ResponseEntity<CategoryResponseDTO> getCategoryBySlug(@PathVariable String slug) {
        Category category = categoryService.getCategoryBySlug(slug);
        return ResponseEntity.ok(CategoryResponseDTO.from(category));
    }

    @GetMapping("/{id}/subcategories")
    public ResponseEntity<List<CategoryResponseDTO>> getSubcategories(@PathVariable String id) {
        List<CategoryResponseDTO> response = categoryService.getSubcategories(CategoryId.of(id)).stream()
                .map(CategoryResponseDTO::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/all")
    public ResponseEntity<List<CategoryResponseDTO>> getAllCategories() {
        List<CategoryResponseDTO> response = categoryService.getAllCategories().stream()
                .map(CategoryResponseDTO::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<CategoryResponseDTO> createCategory(
            @Valid @RequestBody CreateCategoryRequestDTO request) {
        CategoryId parentId = (request.parentId() != null && !request.parentId().isBlank())
                ? CategoryId.of(request.parentId())
                : null;

        Category created = categoryService.createCategory(
                request.name(),
                request.description(),
                request.slug(),
                parentId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CategoryResponseDTO.from(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> updateCategory(
            @PathVariable String id,
            @Valid @RequestBody UpdateCategoryRequestDTO request) {
        Category updated = categoryService.updateCategory(
                CategoryId.of(id),
                request.name(),
                request.description(),
                request.slug());

        return ResponseEntity.ok(CategoryResponseDTO.from(updated));
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<CategoryResponseDTO> activateCategory(@PathVariable String id) {
        Category activated = categoryService.activateCategory(CategoryId.of(id));
        return ResponseEntity.ok(CategoryResponseDTO.from(activated));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<CategoryResponseDTO> deactivateCategory(@PathVariable String id) {
        Category deactivated = categoryService.deactivateCategory(CategoryId.of(id));
        return ResponseEntity.ok(CategoryResponseDTO.from(deactivated));
    }
}
