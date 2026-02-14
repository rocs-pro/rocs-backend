package com.nsbm.rocs.inventory.controller;

import com.nsbm.rocs.inventory.dto.*;
import com.nsbm.rocs.inventory.service.StockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        return ResponseEntity.ok(InventoryResponseBuilder.build(stocks, "Stock retrieved successfully"));
    }

    @GetMapping("/branch/{branchId}")
    public ResponseEntity<Map<String, Object>> getStockByBranch(@PathVariable Long branchId) {
        List<StockDTO> stocks = stockService.getStockByBranch(branchId);
        return ResponseEntity.ok(InventoryResponseBuilder.build(stocks, "Stock retrieved successfully"));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<Map<String, Object>> getStockByProduct(@PathVariable Long productId) {
        List<StockDTO> stocks = stockService.getStockByProduct(productId);
        return ResponseEntity.ok(InventoryResponseBuilder.build(stocks, "Stock retrieved successfully"));
    }

    @GetMapping("/branch/{branchId}/product/{productId}")
    public ResponseEntity<Map<String, Object>> getStockByBranchAndProduct(
            @PathVariable Long branchId,
            @PathVariable Long productId) {
        StockDTO stock = stockService.getStockByBranchAndProduct(branchId, productId);
        return ResponseEntity.ok(InventoryResponseBuilder.build(stock, "Stock retrieved successfully"));
    }

    @PostMapping("/adjust")
    public ResponseEntity<Map<String, Object>> adjustStock(@Valid @RequestBody StockAdjustmentDTO adjustmentDTO) {
        StockDTO stock = stockService.adjustStock(adjustmentDTO);
        return ResponseEntity.ok(InventoryResponseBuilder.build(stock, "Stock adjusted successfully"));
    }

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addStock(
            @RequestParam Long branchId,
            @RequestParam Long productId,
            @RequestParam Integer quantity) {
        StockDTO stock = stockService.addStock(branchId, productId, quantity);
        return ResponseEntity.ok(InventoryResponseBuilder.build(stock, "Stock added successfully"));
    }

    @PostMapping("/remove")
    public ResponseEntity<Map<String, Object>> removeStock(
            @RequestParam Long branchId,
            @RequestParam Long productId,
            @RequestParam Integer quantity) {
        StockDTO stock = stockService.removeStock(branchId, productId, quantity);
        return ResponseEntity.ok(InventoryResponseBuilder.build(stock, "Stock removed successfully"));
    }

    @PostMapping("/reserve")
    public ResponseEntity<Map<String, Object>> reserveStock(
            @RequestParam Long branchId,
            @RequestParam Long productId,
            @RequestParam Integer quantity) {
        StockDTO stock = stockService.reserveStock(branchId, productId, quantity);
        return ResponseEntity.ok(InventoryResponseBuilder.build(stock, "Stock reserved successfully"));
    }

    @PostMapping("/release")
    public ResponseEntity<Map<String, Object>> releaseReservedStock(
            @RequestParam Long branchId,
            @RequestParam Long productId,
            @RequestParam Integer quantity) {
        StockDTO stock = stockService.releaseReservedStock(branchId, productId, quantity);
        return ResponseEntity.ok(InventoryResponseBuilder.build(stock, "Reserved stock released successfully"));
    }

    @GetMapping("/report")
    public ResponseEntity<Map<String, Object>> getStockReport(
            @RequestParam(required = false) Long branchId) {
        List<StockReportDTO> report = stockService.getStockReport(branchId);
        return ResponseEntity.ok(InventoryResponseBuilder.build(report, "Stock report generated successfully"));
    }

    @GetMapping("/alerts/low-stock")
    public ResponseEntity<Map<String, Object>> getLowStockAlerts(
            @RequestParam(required = false) Long branchId) {
        List<LowStockAlertDTO> alerts = stockService.getLowStockAlerts(branchId);
        return ResponseEntity.ok(InventoryResponseBuilder.build(alerts, "Low stock alerts retrieved successfully"));
    }
}
