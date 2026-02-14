package com.nsbm.rocs.pos.controller;

import com.nsbm.rocs.common.response.ApiResponse;
import com.nsbm.rocs.entity.inventory.Product;
import com.nsbm.rocs.entity.inventory.Stock;
import com.nsbm.rocs.entity.main.UserProfile;
import com.nsbm.rocs.inventory.repository.ProductRepository;
import com.nsbm.rocs.inventory.repository.StockRepository;
import com.nsbm.rocs.pos.dto.PosProductDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/pos/products")
@CrossOrigin
public class PosProductController {

    private final ProductRepository productRepository;
    private final StockRepository stockRepository;

    @Autowired
    public PosProductController(ProductRepository productRepository, StockRepository stockRepository) {
        this.productRepository = productRepository;
        this.stockRepository = stockRepository;
    }

    private Long getCurrentUserBranchId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserProfile) {
            UserProfile user = (UserProfile) auth.getPrincipal();
            return user.getBranch() != null ? user.getBranch().getBranchId() : 1L;
        }
        return 1L;
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<PosProductDTO>>> searchProducts(@RequestParam String q) {
        if (q == null || q.trim().isEmpty()) {
            return new ResponseEntity<>(
                    ApiResponse.success("No query provided", List.of()),
                    HttpStatus.OK
            );
        }

        Long branchId = getCurrentUserBranchId();
        List<PosProductDTO> products = productRepository.searchProducts(q).stream()
                .limit(20)
                .map(p -> mapToDTO(p, branchId))
                .collect(Collectors.toList());

        return new ResponseEntity<>(
                ApiResponse.success("Products found", products),
                HttpStatus.OK
        );
    }

    @GetMapping("/{query}")
    public ResponseEntity<ApiResponse<PosProductDTO>> getProduct(@PathVariable String query) {
        Long branchId = getCurrentUserBranchId();

        // 1. Try by ID (if numeric)
        if (query.matches("\\d+")) {
            try {
                Long id = Long.parseLong(query);
                var product = productRepository.findById(id);
                if (product.isPresent()) {
                    PosProductDTO dto = mapToDTO(product.get(), branchId);
                    // Check stock availability
                    if (dto.getAvailableStock() != null && dto.getAvailableStock().compareTo(BigDecimal.ZERO) <= 0) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(ApiResponse.error("Product '" + dto.getName() + "' is out of stock (Quantity: 0)"));
                    }
                    return ResponseEntity.ok(ApiResponse.success("Product found", dto));
                }
            } catch (NumberFormatException ignored) {}
        }

        // 2. Try by Barcode
        var productByBarcode = productRepository.findByBarcode(query);
        if (productByBarcode.isPresent()) {
            PosProductDTO dto = mapToDTO(productByBarcode.get(), branchId);
            if (dto.getAvailableStock() != null && dto.getAvailableStock().compareTo(BigDecimal.ZERO) <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("Product '" + dto.getName() + "' is out of stock (Quantity: 0)"));
            }
            return ResponseEntity.ok(ApiResponse.success("Product found", dto));
        }

        // 3. Try by SKU
        var productBySku = productRepository.findBySku(query);
        if (productBySku.isPresent()) {
            PosProductDTO dto = mapToDTO(productBySku.get(), branchId);
            if (dto.getAvailableStock() != null && dto.getAvailableStock().compareTo(BigDecimal.ZERO) <= 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.error("Product '" + dto.getName() + "' is out of stock (Quantity: 0)"));
            }
            return ResponseEntity.ok(ApiResponse.success("Product found", dto));
        }

        return new ResponseEntity<>(
                ApiResponse.error("Product not found"),
                HttpStatus.NOT_FOUND);
    }

    @GetMapping("/quick")
    public ResponseEntity<ApiResponse<List<PosProductDTO>>> getQuickItems() {
        Long branchId = getCurrentUserBranchId();
        // Return popular or quick access items with stock info
        List<PosProductDTO> products = productRepository.findByIsActiveTrue(org.springframework.data.domain.PageRequest.of(0, 10))
                .getContent()
                .stream()
                .map(p -> mapToDTO(p, branchId))
                .collect(Collectors.toList());
        return new ResponseEntity<>(
                ApiResponse.success("Quick items", products),
                HttpStatus.OK
        );
    }

    /**
     * Get stock availability for a specific product
     */
    @GetMapping("/{productId}/stock")
    public ResponseEntity<ApiResponse<BigDecimal>> getProductStock(
            @PathVariable Long productId,
            @RequestParam(required = false) Long branchId) {
        Long targetBranchId = branchId != null ? branchId : getCurrentUserBranchId();
        
        BigDecimal availableStock = stockRepository.findByBranchIdAndProductId(targetBranchId, productId)
                .map(Stock::getAvailableQty)
                .orElse(BigDecimal.ZERO);
        
        return ResponseEntity.ok(ApiResponse.success("Stock fetched", availableStock));
    }

    private PosProductDTO mapToDTO(Product product, Long branchId) {
        PosProductDTO dto = new PosProductDTO();
        dto.setProductId(product.getProductId());
        dto.setName(product.getName());
        dto.setSellingPrice(product.getSellingPrice());
        dto.setSku(product.getSku());
        dto.setBarcode(product.getBarcode());
        dto.setTaxRate(product.getTaxRate());
        dto.setIsSerialized(product.getIsSerialized());
        
        // Fetch available stock for the branch
        BigDecimal availableStock = stockRepository.findByBranchIdAndProductId(branchId, product.getProductId())
                .map(Stock::getAvailableQty)
                .orElse(BigDecimal.ZERO);
        dto.setAvailableStock(availableStock);
        
        return dto;
    }
}

