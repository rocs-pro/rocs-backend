package com.nsbm.rocs.inventory.controller;

import com.nsbm.rocs.inventory.dto.SubCategoryDTO;
import com.nsbm.rocs.inventory.service.SubCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventory/subcategories")
@RequiredArgsConstructor
public class SubCategoryController {

    private final SubCategoryService subCategoryService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllSubCategories() {
        List<SubCategoryDTO> subCategories = subCategoryService.getAllSubCategories();
        return ResponseEntity.ok(InventoryResponseBuilder.build(subCategories, "Subcategories retrieved successfully"));
    }

    @GetMapping("/active")
    public ResponseEntity<Map<String, Object>> getActiveSubCategories() {
        List<SubCategoryDTO> subCategories = subCategoryService.getActiveSubCategories();
        return ResponseEntity.ok(InventoryResponseBuilder.build(subCategories, "Active subcategories retrieved successfully"));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Map<String, Object>> getSubCategoriesByCategoryId(@PathVariable Long categoryId) {
        List<SubCategoryDTO> subCategories = subCategoryService.getSubCategoriesByCategoryId(categoryId);
        return ResponseEntity.ok(InventoryResponseBuilder.build(subCategories, "Subcategories retrieved successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getSubCategoryById(@PathVariable Long id) {
        SubCategoryDTO subCategory = subCategoryService.getSubCategoryById(id);
        return ResponseEntity.ok(InventoryResponseBuilder.build(subCategory, "Subcategory retrieved successfully"));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createSubCategory(@Valid @RequestBody SubCategoryDTO subCategoryDTO) {
        SubCategoryDTO createdSubCategory = subCategoryService.createSubCategory(subCategoryDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(InventoryResponseBuilder.build(createdSubCategory, "Subcategory created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateSubCategory(
            @PathVariable Long id,
            @Valid @RequestBody SubCategoryDTO subCategoryDTO) {
        SubCategoryDTO updatedSubCategory = subCategoryService.updateSubCategory(id, subCategoryDTO);
        return ResponseEntity.ok(InventoryResponseBuilder.build(updatedSubCategory, "Subcategory updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteSubCategory(@PathVariable Long id) {
        subCategoryService.deleteSubCategory(id);
        return ResponseEntity.ok(InventoryResponseBuilder.buildMessage("Subcategory deleted successfully"));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Map<String, Object>> deactivateSubCategory(@PathVariable Long id) {
        subCategoryService.deactivateSubCategory(id);
        return ResponseEntity.ok(InventoryResponseBuilder.buildMessage("Subcategory deactivated successfully"));
    }
}
