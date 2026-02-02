package com.nsbm.rocs.pos.controller;

import com.nsbm.rocs.common.response.ApiResponse;
import com.nsbm.rocs.entity.inventory.Product;
import com.nsbm.rocs.inventory.repository.ProductRepository;
import com.nsbm.rocs.pos.dto.PosProductDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/pos/products")
@CrossOrigin
public class PosProductController {

    private final ProductRepository productRepository;

    @Autowired
    public PosProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<PosProductDTO>>> searchProducts(@RequestParam String q) {
        if (q == null || q.trim().isEmpty()) {
            return new ResponseEntity<>(
                    ApiResponse.success("No query provided", List.of()),
                    HttpStatus.OK
            );
        }

        List<PosProductDTO> products = productRepository.searchProducts(q).stream()
                .limit(20)
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        return new ResponseEntity<>(
                ApiResponse.success("Products found", products),
                HttpStatus.OK
        );
    }

    @GetMapping("/{query}")
    public ResponseEntity<ApiResponse<PosProductDTO>> getProduct(@PathVariable String query) {
        // 1. Try by ID (if numeric)
        if (query.matches("\\d+")) {
            try {
                Long id = Long.parseLong(query);
                var product = productRepository.findById(id);
                if (product.isPresent()) {
                    return ResponseEntity.ok(ApiResponse.success("Product found", mapToDTO(product.get())));
                }
            } catch (NumberFormatException ignored) {}
        }

        // 2. Try by Barcode
        var productByBarcode = productRepository.findByBarcode(query);
        if (productByBarcode.isPresent()) {
             return ResponseEntity.ok(ApiResponse.success("Product found", mapToDTO(productByBarcode.get())));
        }

        // 3. Try by SKU
        var productBySku = productRepository.findBySku(query);
        return productBySku.map(product -> ResponseEntity.ok(ApiResponse.success("Product found", mapToDTO(product)))).orElseGet(() -> new ResponseEntity<>(
                ApiResponse.error("Product not found"),
                HttpStatus.NOT_FOUND));

    }

    @GetMapping("/quick")
    public ResponseEntity<ApiResponse<List<PosProductDTO>>> getQuickItems() {
        // Return popular or quick access items
        // For now, return first 10 active items using pagination
        List<PosProductDTO> products = productRepository.findByIsActiveTrue(org.springframework.data.domain.PageRequest.of(0, 10))
                .getContent()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        return new ResponseEntity<>(
                ApiResponse.success("Quick items", products),
                HttpStatus.OK
        );
    }

    private PosProductDTO mapToDTO(Product product) {
        PosProductDTO dto = new PosProductDTO();
        dto.setProductId(product.getProductId());
        dto.setName(product.getName());
        dto.setSellingPrice(product.getSellingPrice());
        dto.setSku(product.getSku());
        dto.setBarcode(product.getBarcode());
        dto.setTaxRate(product.getTaxRate());
        dto.setIsSerialized(product.getIsSerialized());
        return dto;
    }
}
