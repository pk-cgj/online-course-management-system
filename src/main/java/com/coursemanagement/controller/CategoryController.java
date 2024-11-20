package com.coursemanagement.controller;

import com.coursemanagement.dto.CategoryDto;
import com.coursemanagement.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Category Management Controller", description = "APIs for managing categories")
public class CategoryController {
    private final CategoryService categoryService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @Operation(summary = "Create a category", tags = {"Admin"})
    public ResponseEntity<CategoryDto> createCategory(
            @Valid @RequestBody CategoryDto.CreateCategoryDto createCategoryDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(categoryService.createCategory(createCategoryDto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{categoryId}")
    @Operation(summary = "Update a category", tags = {"Admin"})
    public ResponseEntity<CategoryDto> updateCategory(
            @PathVariable Long categoryId,
            @Valid @RequestBody CategoryDto.UpdateCategoryDto updateCategoryDto) {
        return ResponseEntity.ok(categoryService.updateCategory(categoryId, updateCategoryDto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{categoryId}")
    @Operation(summary = "Delete a category", tags = {"Admin"})
    public ResponseEntity<Void> deleteCategory(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{categoryId}")
    @Operation(summary = "Get a category", tags = {"Public"})
    public ResponseEntity<CategoryDto> getCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(categoryService.getCategory(categoryId));
    }

    @GetMapping
    @Operation(summary = "Get all categories", tags = {"Public"})
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }
}
