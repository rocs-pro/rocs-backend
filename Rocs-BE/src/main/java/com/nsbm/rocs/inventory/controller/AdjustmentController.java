package com.nsbm.rocs.inventory.controller;

import com.nsbm.rocs.inventory.dto.StockAdjustmentDTO;
import com.nsbm.rocs.inventory.service.AdjustmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventory/adjustments")
@RequiredArgsConstructor
public class AdjustmentController {

    private final AdjustmentService adjustmentService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllAdjustments(
            @RequestParam(required = false) Long branchId,
            @RequestParam(required = false) Long productId) {
        List<StockAdjustmentDTO> adjustments = adjustmentService.getAllAdjustments(branchId, productId);
        return ResponseEntity.ok(InventoryResponseBuilder.build(adjustments, "Adjustments retrieved successfully"));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createAdjustment(@Valid @RequestBody StockAdjustmentDTO adjustmentDTO) {
        StockAdjustmentDTO created = adjustmentService.createAdjustment(adjustmentDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(InventoryResponseBuilder.build(created, "Adjustment created successfully"));
    }
}

