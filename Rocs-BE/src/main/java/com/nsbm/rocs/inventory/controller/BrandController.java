package com.nsbm.rocs.inventory.controller;

import com.nsbm.rocs.inventory.dto.BrandDTO;
import com.nsbm.rocs.inventory.service.BrandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", brands);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    public ResponseEntity<Map<String, Object>> getActiveBrands() {
        List<BrandDTO> brands = brandService.getActiveBrands();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", brands);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getBrandById(@PathVariable Long id) {
        BrandDTO brand = brandService.getBrandById(id);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", brand);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createBrand(@Valid @RequestBody BrandDTO brandDTO) {
        BrandDTO createdBrand = brandService.createBrand(brandDTO);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Brand created successfully");
        response.put("data", createdBrand);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateBrand(
            @PathVariable Long id,
            @Valid @RequestBody BrandDTO brandDTO) {
        BrandDTO updatedBrand = brandService.updateBrand(id, brandDTO);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Brand updated successfully");
        response.put("data", updatedBrand);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteBrand(@PathVariable Long id) {
        brandService.deleteBrand(id);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Brand deleted successfully");
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Map<String, Object>> deactivateBrand(@PathVariable Long id) {
        brandService.deactivateBrand(id);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Brand deactivated successfully");
        return ResponseEntity.ok(response);
    }
}

