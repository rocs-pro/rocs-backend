package com.nsbm.rocs.inventory.controller;

import com.nsbm.rocs.inventory.dto.DamagedProductDTO;
import com.nsbm.rocs.inventory.dto.ProductSerialDTO;
import com.nsbm.rocs.inventory.service.ProductSerialService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        return ResponseEntity.ok(InventoryResponseBuilder.build(serials, "Serials retrieved successfully"));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<Map<String, Object>> getSerialsByProduct(@PathVariable Long productId) {
        List<ProductSerialDTO> serials = productSerialService.getSerialsByProduct(productId);
        return ResponseEntity.ok(InventoryResponseBuilder.build(serials, "Serials retrieved successfully"));
    }

    @GetMapping("/branch/{branchId}")
    public ResponseEntity<Map<String, Object>> getSerialsByBranch(@PathVariable Long branchId) {
        List<ProductSerialDTO> serials = productSerialService.getSerialsByBranch(branchId);
        return ResponseEntity.ok(InventoryResponseBuilder.build(serials, "Serials retrieved successfully"));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Map<String, Object>> getSerialsByStatus(@PathVariable String status) {
        List<ProductSerialDTO> serials = productSerialService.getSerialsByStatus(status);
        return ResponseEntity.ok(InventoryResponseBuilder.build(serials, "Serials retrieved successfully"));
    }

    @GetMapping("/available")
    public ResponseEntity<Map<String, Object>> getAvailableSerials(
            @RequestParam Long branchId,
            @RequestParam Long productId) {
        List<ProductSerialDTO> serials = productSerialService.getAvailableSerials(branchId, productId);
        return ResponseEntity.ok(InventoryResponseBuilder.build(serials, "Available serials retrieved successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getSerialById(@PathVariable Long id) {
        ProductSerialDTO serial = productSerialService.getSerialById(id);
        return ResponseEntity.ok(InventoryResponseBuilder.build(serial, "Serial retrieved successfully"));
    }

    @GetMapping("/serial/{serialNo}")
    public ResponseEntity<Map<String, Object>> getSerialBySerialNo(@PathVariable String serialNo) {
        ProductSerialDTO serial = productSerialService.getSerialBySerialNo(serialNo);
        return ResponseEntity.ok(InventoryResponseBuilder.build(serial, "Serial retrieved successfully"));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createSerial(@Valid @RequestBody ProductSerialDTO serialDTO) {
        ProductSerialDTO createdSerial = productSerialService.createSerial(serialDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(InventoryResponseBuilder.build(createdSerial, "Serial created successfully"));
    }

    @PostMapping("/bulk")
    public ResponseEntity<Map<String, Object>> createBulkSerials(
            @Valid @RequestBody List<ProductSerialDTO> serialDTOs) {
        List<ProductSerialDTO> createdSerials = productSerialService.createBulkSerials(serialDTOs);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(InventoryResponseBuilder.build(createdSerials, "Serials created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateSerial(
            @PathVariable Long id,
            @Valid @RequestBody ProductSerialDTO serialDTO) {
        ProductSerialDTO updatedSerial = productSerialService.updateSerial(id, serialDTO);
        return ResponseEntity.ok(InventoryResponseBuilder.build(updatedSerial, "Serial updated successfully"));
    }

    @PatchMapping("/{id}/sold")
    public ResponseEntity<Map<String, Object>> markAsSold(
            @PathVariable Long id,
            @RequestParam Long saleId) {
        ProductSerialDTO serial = productSerialService.markAsSold(id, saleId);
        return ResponseEntity.ok(InventoryResponseBuilder.build(serial, "Serial marked as sold"));
    }

    @PatchMapping("/{id}/damaged")
    public ResponseEntity<Map<String, Object>> markAsDamaged(@PathVariable Long id) {
        ProductSerialDTO serial = productSerialService.markAsDamaged(id);
        return ResponseEntity.ok(InventoryResponseBuilder.build(serial, "Serial marked as damaged"));
    }

    @PatchMapping("/{id}/returned")
    public ResponseEntity<Map<String, Object>> markAsReturned(@PathVariable Long id) {
        ProductSerialDTO serial = productSerialService.markAsReturned(id);
        return ResponseEntity.ok(InventoryResponseBuilder.build(serial, "Serial marked as returned"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteSerial(@PathVariable Long id) {
        productSerialService.deleteSerial(id);
        return ResponseEntity.ok(InventoryResponseBuilder.buildMessage("Serial deleted successfully"));
    }

    @GetMapping("/damaged")
    public ResponseEntity<Map<String, Object>> getDamagedProducts(
            @RequestParam(required = false) Long branchId) {
        List<DamagedProductDTO> damagedProducts = productSerialService.getDamagedProducts(branchId);
        return ResponseEntity.ok(InventoryResponseBuilder.build(damagedProducts, "Damaged products retrieved successfully"));
    }
}
