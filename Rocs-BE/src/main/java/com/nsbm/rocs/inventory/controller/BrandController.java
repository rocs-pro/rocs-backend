package com.nsbm.rocs.inventory.controller;

import com.nsbm.rocs.inventory.dto.BrandDTO;
import com.nsbm.rocs.inventory.service.BrandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventory/brands")
@RequiredArgsConstructor
public class BrandController {

    private final BrandService brandService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllBrands() {
        List<BrandDTO> brands = brandService.getAllBrands();
        return ResponseEntity.ok(InventoryResponseBuilder.build(brands, "Brands retrieved successfully"));
    }

    @GetMapping("/active")
    public ResponseEntity<Map<String, Object>> getActiveBrands() {
        List<BrandDTO> brands = brandService.getActiveBrands();
        return ResponseEntity.ok(InventoryResponseBuilder.build(brands, "Active brands retrieved successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getBrandById(@PathVariable Long id) {
        BrandDTO brand = brandService.getBrandById(id);
        return ResponseEntity.ok(InventoryResponseBuilder.build(brand, "Brand retrieved successfully"));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createBrand(@Valid @RequestBody BrandDTO brandDTO) {
        BrandDTO createdBrand = brandService.createBrand(brandDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(InventoryResponseBuilder.build(createdBrand, "Brand created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateBrand(
            @PathVariable Long id,
            @Valid @RequestBody BrandDTO brandDTO) {
        BrandDTO updatedBrand = brandService.updateBrand(id, brandDTO);
        return ResponseEntity.ok(InventoryResponseBuilder.build(updatedBrand, "Brand updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteBrand(@PathVariable Long id) {
        brandService.deleteBrand(id);
        return ResponseEntity.ok(InventoryResponseBuilder.buildMessage("Brand deleted successfully"));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Map<String, Object>> deactivateBrand(@PathVariable Long id) {
        brandService.deactivateBrand(id);
        return ResponseEntity.ok(InventoryResponseBuilder.buildMessage("Brand deactivated successfully"));
    }
}
