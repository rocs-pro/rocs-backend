package com.nsbm.rocs.inventory.controller;

import com.nsbm.rocs.inventory.dto.*;
import com.nsbm.rocs.inventory.service.StockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventory/stock")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllStock() {
        List<StockDTO> stocks = stockService.getAllStock();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", stocks);
        response.put("count", stocks.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/branch/{branchId}")
    public ResponseEntity<Map<String, Object>> getStockByBranch(@PathVariable Long branchId) {
        List<StockDTO> stocks = stockService.getStockByBranch(branchId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", stocks);
        response.put("count", stocks.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<Map<String, Object>> getStockByProduct(@PathVariable Long productId) {
        List<StockDTO> stocks = stockService.getStockByProduct(productId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", stocks);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/branch/{branchId}/product/{productId}")
    public ResponseEntity<Map<String, Object>> getStockByBranchAndProduct(
            @PathVariable Long branchId,
            @PathVariable Long productId) {
        StockDTO stock = stockService.getStockByBranchAndProduct(branchId, productId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", stock);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/adjust")
    public ResponseEntity<Map<String, Object>> adjustStock(@Valid @RequestBody StockAdjustmentDTO adjustmentDTO) {
        StockDTO stock = stockService.adjustStock(adjustmentDTO);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Stock adjusted successfully");
        response.put("data", stock);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addStock(
            @RequestParam Long branchId,
            @RequestParam Long productId,
            @RequestParam Integer quantity) {
        StockDTO stock = stockService.addStock(branchId, productId, quantity);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Stock added successfully");
        response.put("data", stock);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/remove")
    public ResponseEntity<Map<String, Object>> removeStock(
            @RequestParam Long branchId,
            @RequestParam Long productId,
            @RequestParam Integer quantity) {
        StockDTO stock = stockService.removeStock(branchId, productId, quantity);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Stock removed successfully");
        response.put("data", stock);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reserve")
    public ResponseEntity<Map<String, Object>> reserveStock(
            @RequestParam Long branchId,
            @RequestParam Long productId,
            @RequestParam Integer quantity) {
        StockDTO stock = stockService.reserveStock(branchId, productId, quantity);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Stock reserved successfully");
        response.put("data", stock);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/release")
    public ResponseEntity<Map<String, Object>> releaseReservedStock(
            @RequestParam Long branchId,
            @RequestParam Long productId,
            @RequestParam Integer quantity) {
        StockDTO stock = stockService.releaseReservedStock(branchId, productId, quantity);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Reserved stock released successfully");
        response.put("data", stock);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/report")
    public ResponseEntity<Map<String, Object>> getStockReport(
            @RequestParam(required = false) Long branchId) {
        List<StockReportDTO> report = stockService.getStockReport(branchId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", report);
        response.put("count", report.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/alerts/low-stock")
    public ResponseEntity<Map<String, Object>> getLowStockAlerts(
            @RequestParam(required = false) Long branchId) {
        List<LowStockAlertDTO> alerts = stockService.getLowStockAlerts(branchId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", alerts);
        response.put("count", alerts.size());
        return ResponseEntity.ok(response);
    }
}

