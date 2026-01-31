package com.nsbm.rocs.inventory.controller;

import com.nsbm.rocs.inventory.dto.BatchDTO;
import com.nsbm.rocs.inventory.dto.ExpiryAlertDTO;
import com.nsbm.rocs.inventory.service.BatchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", batches);
        response.put("count", batches.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<Map<String, Object>> getBatchesByProduct(@PathVariable Long productId) {
        List<BatchDTO> batches = batchService.getBatchesByProduct(productId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", batches);
        response.put("count", batches.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/branch/{branchId}")
    public ResponseEntity<Map<String, Object>> getBatchesByBranch(@PathVariable Long branchId) {
        List<BatchDTO> batches = batchService.getBatchesByBranch(branchId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", batches);
        response.put("count", batches.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getBatchById(@PathVariable Long id) {
        BatchDTO batch = batchService.getBatchById(id);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", batch);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/branch/{branchId}/product/{productId}/code/{batchCode}")
    public ResponseEntity<Map<String, Object>> getBatchByCode(
            @PathVariable Long branchId,
            @PathVariable Long productId,
            @PathVariable String batchCode) {
        BatchDTO batch = batchService.getBatchByCode(branchId, productId, batchCode);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", batch);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/expired")
    public ResponseEntity<Map<String, Object>> getExpiredBatches() {
        List<BatchDTO> batches = batchService.getExpiredBatches();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", batches);
        response.put("count", batches.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/expiring-soon")
    public ResponseEntity<Map<String, Object>> getExpiringSoonBatches(
            @RequestParam(defaultValue = "30") int days) {
        List<BatchDTO> batches = batchService.getExpiringSoonBatches(days);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", batches);
        response.put("count", batches.size());
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createBatch(@Valid @RequestBody BatchDTO batchDTO) {
        BatchDTO createdBatch = batchService.createBatch(batchDTO);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Batch created successfully");
        response.put("data", createdBatch);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateBatch(
            @PathVariable Long id,
            @Valid @RequestBody BatchDTO batchDTO) {
        BatchDTO updatedBatch = batchService.updateBatch(id, batchDTO);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Batch updated successfully");
        response.put("data", updatedBatch);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteBatch(@PathVariable Long id) {
        batchService.deleteBatch(id);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Batch deleted successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/alerts/expiry")
    public ResponseEntity<Map<String, Object>> getExpiryAlerts(
            @RequestParam(defaultValue = "30") int warningDays,
            @RequestParam(defaultValue = "7") int criticalDays) {
        List<ExpiryAlertDTO> alerts = batchService.getExpiryAlerts(warningDays, criticalDays);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", alerts);
        response.put("count", alerts.size());
        return ResponseEntity.ok(response);
    }
}
