package com.nsbm.rocs.inventory.controller;

import com.nsbm.rocs.inventory.dto.StockDTO;
import com.nsbm.rocs.inventory.service.StockOverviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventory/stock-overview")
@RequiredArgsConstructor
public class StockOverviewController {

    private final StockOverviewService stockOverviewService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getStockOverview(
            @RequestParam(required = false) Long branchId) {
        List<StockDTO> stockData = stockOverviewService.getStockOverview(branchId);
        return ResponseEntity.ok(InventoryResponseBuilder.build(stockData, "Stock overview retrieved successfully"));
    }

    @GetMapping("/low-stock")
    public ResponseEntity<Map<String, Object>> getLowStockProducts(
            @RequestParam Long branchId,
            @RequestParam(defaultValue = "10") Integer threshold) {
        List<StockDTO> lowStock = stockOverviewService.getLowStockProducts(branchId, threshold);
        return ResponseEntity.ok(InventoryResponseBuilder.build(lowStock, "Low stock products retrieved successfully"));
    }
}

