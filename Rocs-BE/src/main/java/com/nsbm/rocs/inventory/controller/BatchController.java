package com.nsbm.rocs.inventory.controller;

import com.nsbm.rocs.inventory.dto.BatchDTO;
import com.nsbm.rocs.inventory.dto.ExpiryAlertDTO;
import com.nsbm.rocs.inventory.service.BatchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventory/batches")
@RequiredArgsConstructor
public class BatchController {

    private final BatchService batchService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllBatches() {
        List<BatchDTO> batches = batchService.getAllBatches();
        return ResponseEntity.ok(InventoryResponseBuilder.build(batches, "Batches retrieved successfully"));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<Map<String, Object>> getBatchesByProduct(@PathVariable Long productId) {
        List<BatchDTO> batches = batchService.getBatchesByProduct(productId);
        return ResponseEntity.ok(InventoryResponseBuilder.build(batches, "Batches retrieved successfully"));
    }

    @GetMapping("/branch/{branchId}")
    public ResponseEntity<Map<String, Object>> getBatchesByBranch(@PathVariable Long branchId) {
        List<BatchDTO> batches = batchService.getBatchesByBranch(branchId);
        return ResponseEntity.ok(InventoryResponseBuilder.build(batches, "Batches retrieved successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getBatchById(@PathVariable Long id) {
        BatchDTO batch = batchService.getBatchById(id);
        return ResponseEntity.ok(InventoryResponseBuilder.build(batch, "Batch retrieved successfully"));
    }

    @GetMapping("/branch/{branchId}/product/{productId}/code/{batchCode}")
    public ResponseEntity<Map<String, Object>> getBatchByCode(
            @PathVariable Long branchId,
            @PathVariable Long productId,
            @PathVariable String batchCode) {
        BatchDTO batch = batchService.getBatchByCode(branchId, productId, batchCode);
        return ResponseEntity.ok(InventoryResponseBuilder.build(batch, "Batch retrieved successfully"));
    }

    @GetMapping("/expired")
    public ResponseEntity<Map<String, Object>> getExpiredBatches() {
        List<BatchDTO> batches = batchService.getExpiredBatches();
        return ResponseEntity.ok(InventoryResponseBuilder.build(batches, "Expired batches retrieved successfully"));
    }

    @GetMapping("/expiring-soon")
    public ResponseEntity<Map<String, Object>> getExpiringSoonBatches(
            @RequestParam(defaultValue = "30") int days) {
        List<BatchDTO> batches = batchService.getExpiringSoonBatches(days);
        return ResponseEntity.ok(InventoryResponseBuilder.build(batches, "Expiring batches retrieved successfully"));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createBatch(@Valid @RequestBody BatchDTO batchDTO) {
        BatchDTO createdBatch = batchService.createBatch(batchDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(InventoryResponseBuilder.build(createdBatch, "Batch created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateBatch(
            @PathVariable Long id,
            @Valid @RequestBody BatchDTO batchDTO) {
        BatchDTO updatedBatch = batchService.updateBatch(id, batchDTO);
        return ResponseEntity.ok(InventoryResponseBuilder.build(updatedBatch, "Batch updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteBatch(@PathVariable Long id) {
        batchService.deleteBatch(id);
        return ResponseEntity.ok(InventoryResponseBuilder.buildMessage("Batch deleted successfully"));
    }

    @GetMapping("/alerts/expiry")
    public ResponseEntity<Map<String, Object>> getExpiryAlerts(
            @RequestParam(defaultValue = "30") int warningDays,
            @RequestParam(defaultValue = "7") int criticalDays) {
        List<ExpiryAlertDTO> alerts = batchService.getExpiryAlerts(warningDays, criticalDays);
        return ResponseEntity.ok(InventoryResponseBuilder.build(alerts, "Expiry alerts retrieved successfully"));
    }
}
