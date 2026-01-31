package com.nsbm.rocs.inventory.controller;

import com.nsbm.rocs.inventory.dto.ProductDTO;
import com.nsbm.rocs.inventory.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventory/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllProducts() {
        List<ProductDTO> products = productService.getAllProducts();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", products);
        response.put("count", products.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    public ResponseEntity<Map<String, Object>> getActiveProducts() {
        List<ProductDTO> products = productService.getActiveProducts();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", products);
        response.put("count", products.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getProductById(@PathVariable Long id) {
        ProductDTO product = productService.getProductById(id);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", product);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/sku/{sku}")
    public ResponseEntity<Map<String, Object>> getProductBySku(@PathVariable String sku) {
        ProductDTO product = productService.getProductBySku(sku);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", product);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/barcode/{barcode}")
    public ResponseEntity<Map<String, Object>> getProductByBarcode(@PathVariable String barcode) {
        ProductDTO product = productService.getProductByBarcode(barcode);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", product);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Map<String, Object>> getProductsByCategory(@PathVariable Long categoryId) {
        List<ProductDTO> products = productService.getProductsByCategory(categoryId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", products);
        response.put("count", products.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/subcategory/{subCategoryId}")
    public ResponseEntity<Map<String, Object>> getProductsBySubCategory(@PathVariable Long subCategoryId) {
        List<ProductDTO> products = productService.getProductsBySubCategory(subCategoryId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", products);
        response.put("count", products.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/brand/{brandId}")
    public ResponseEntity<Map<String, Object>> getProductsByBrand(@PathVariable Long brandId) {
        List<ProductDTO> products = productService.getProductsByBrand(brandId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", products);
        response.put("count", products.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchProducts(@RequestParam String keyword) {
        List<ProductDTO> products = productService.searchProducts(keyword);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", products);
        response.put("count", products.size());
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createProduct(@Valid @RequestBody ProductDTO productDTO) {
        ProductDTO createdProduct = productService.createProduct(productDTO);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Product created successfully");
        response.put("data", createdProduct);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductDTO productDTO) {
        ProductDTO updatedProduct = productService.updateProduct(id, productDTO);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Product updated successfully");
        response.put("data", updatedProduct);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Product deleted successfully");
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Map<String, Object>> deactivateProduct(@PathVariable Long id) {
        productService.deactivateProduct(id);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Product deactivated successfully");
        return ResponseEntity.ok(response);
    }
}

