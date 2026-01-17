package com.nsbm.rocs.inventory.controller;

import com.nsbm.rocs.inventory.dto.DamagedProductDTO;
import com.nsbm.rocs.inventory.dto.ProductSerialDTO;
import com.nsbm.rocs.inventory.service.ProductSerialService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventory/serials")
@RequiredArgsConstructor
public class ProductSerialController {

    private final ProductSerialService productSerialService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllSerials() {
        List<ProductSerialDTO> serials = productSerialService.getAllSerials();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", serials);
        response.put("count", serials.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<Map<String, Object>> getSerialsByProduct(@PathVariable Long productId) {
        List<ProductSerialDTO> serials = productSerialService.getSerialsByProduct(productId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", serials);
        response.put("count", serials.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/branch/{branchId}")
    public ResponseEntity<Map<String, Object>> getSerialsByBranch(@PathVariable Long branchId) {
        List<ProductSerialDTO> serials = productSerialService.getSerialsByBranch(branchId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", serials);
        response.put("count", serials.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Map<String, Object>> getSerialsByStatus(@PathVariable String status) {
        List<ProductSerialDTO> serials = productSerialService.getSerialsByStatus(status);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", serials);
        response.put("count", serials.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/available")
    public ResponseEntity<Map<String, Object>> getAvailableSerials(
            @RequestParam Long branchId,
            @RequestParam Long productId) {
        List<ProductSerialDTO> serials = productSerialService.getAvailableSerials(branchId, productId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", serials);
        response.put("count", serials.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getSerialById(@PathVariable Long id) {
        ProductSerialDTO serial = productSerialService.getSerialById(id);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", serial);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/serial/{serialNo}")
    public ResponseEntity<Map<String, Object>> getSerialBySerialNo(@PathVariable String serialNo) {
        ProductSerialDTO serial = productSerialService.getSerialBySerialNo(serialNo);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", serial);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createSerial(@Valid @RequestBody ProductSerialDTO serialDTO) {
        ProductSerialDTO createdSerial = productSerialService.createSerial(serialDTO);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Serial created successfully");
        response.put("data", createdSerial);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/bulk")
    public ResponseEntity<Map<String, Object>> createBulkSerials(
            @Valid @RequestBody List<ProductSerialDTO> serialDTOs) {
        List<ProductSerialDTO> createdSerials = productSerialService.createBulkSerials(serialDTOs);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Serials created successfully");
        response.put("data", createdSerials);
        response.put("count", createdSerials.size());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateSerial(
            @PathVariable Long id,
            @Valid @RequestBody ProductSerialDTO serialDTO) {
        ProductSerialDTO updatedSerial = productSerialService.updateSerial(id, serialDTO);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Serial updated successfully");
        response.put("data", updatedSerial);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/sold")
    public ResponseEntity<Map<String, Object>> markAsSold(
            @PathVariable Long id,
            @RequestParam Long saleId) {
        ProductSerialDTO serial = productSerialService.markAsSold(id, saleId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Serial marked as sold");
        response.put("data", serial);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/damaged")
    public ResponseEntity<Map<String, Object>> markAsDamaged(@PathVariable Long id) {
        ProductSerialDTO serial = productSerialService.markAsDamaged(id);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Serial marked as damaged");
        response.put("data", serial);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/returned")
    public ResponseEntity<Map<String, Object>> markAsReturned(@PathVariable Long id) {
        ProductSerialDTO serial = productSerialService.markAsReturned(id);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Serial marked as returned");
        response.put("data", serial);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteSerial(@PathVariable Long id) {
        productSerialService.deleteSerial(id);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Serial deleted successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/damaged")
    public ResponseEntity<Map<String, Object>> getDamagedProducts(
            @RequestParam(required = false) Long branchId) {
        List<DamagedProductDTO> damagedProducts = productSerialService.getDamagedProducts(branchId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", damagedProducts);
        response.put("count", damagedProducts.size());
        return ResponseEntity.ok(response);
    }
}

