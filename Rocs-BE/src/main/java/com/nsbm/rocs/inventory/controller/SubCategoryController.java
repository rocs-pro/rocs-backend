package com.nsbm.rocs.inventory.controller;

import com.nsbm.rocs.inventory.dto.SubCategoryDTO;
import com.nsbm.rocs.inventory.service.SubCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", subCategories);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    public ResponseEntity<Map<String, Object>> getActiveSubCategories() {
        List<SubCategoryDTO> subCategories = subCategoryService.getActiveSubCategories();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", subCategories);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Map<String, Object>> getSubCategoriesByCategoryId(@PathVariable Long categoryId) {
        List<SubCategoryDTO> subCategories = subCategoryService.getSubCategoriesByCategoryId(categoryId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", subCategories);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getSubCategoryById(@PathVariable Long id) {
        SubCategoryDTO subCategory = subCategoryService.getSubCategoryById(id);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", subCategory);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createSubCategory(@Valid @RequestBody SubCategoryDTO subCategoryDTO) {
        SubCategoryDTO createdSubCategory = subCategoryService.createSubCategory(subCategoryDTO);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "SubCategory created successfully");
        response.put("data", createdSubCategory);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateSubCategory(
            @PathVariable Long id,
            @Valid @RequestBody SubCategoryDTO subCategoryDTO) {
        SubCategoryDTO updatedSubCategory = subCategoryService.updateSubCategory(id, subCategoryDTO);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "SubCategory updated successfully");
        response.put("data", updatedSubCategory);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteSubCategory(@PathVariable Long id) {
        subCategoryService.deleteSubCategory(id);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "SubCategory deleted successfully");
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Map<String, Object>> deactivateSubCategory(@PathVariable Long id) {
        subCategoryService.deactivateSubCategory(id);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "SubCategory deactivated successfully");
        return ResponseEntity.ok(response);
    }
}

