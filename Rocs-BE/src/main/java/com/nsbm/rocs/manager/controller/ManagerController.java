package com.nsbm.rocs.manager.controller;

import com.nsbm.rocs.manager.dto.*;
import com.nsbm.rocs.manager.service.ManagerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Manager Dashboard endpoints.
 * Base path: /api/inventory/manager
 */
@Slf4j
@RestController
@RequestMapping("/api/inventory/manager")
@RequiredArgsConstructor
public class ManagerController {

    private final ManagerService managerService;

    // ===== DASHBOARD STATS =====

    @GetMapping("/stats")
    public ResponseEntity<List<DashboardStatsDTO>> getDashboardStats() {
        log.info("Fetching dashboard stats");
        List<DashboardStatsDTO> stats = managerService.getDashboardStats();
        return ResponseEntity.ok(stats);
    }

    // ===== SALES DATA =====

    @GetMapping("/sales")
    public ResponseEntity<List<SalesDataDTO>> getSalesData(
            @RequestParam(defaultValue = "weekly") String period) {
        log.info("Fetching sales data for period: {}", period);
        List<SalesDataDTO> salesData = managerService.getSalesData(period);
        return ResponseEntity.ok(salesData);
    }

    // ===== TOP SELLING PRODUCTS =====

    @GetMapping("/products/top-selling")
    public ResponseEntity<List<TopSellingProductDTO>> getTopSellingProducts(
            @RequestParam(defaultValue = "5") int limit) {
        log.info("Fetching top {} selling products", limit);
        List<TopSellingProductDTO> products = managerService.getTopSellingProducts(limit);
        return ResponseEntity.ok(products);
    }

    // ===== PENDING GRNs =====

    @GetMapping("/grns/pending")
    public ResponseEntity<List<PendingGrnDTO>> getPendingGrns() {
        log.info("Fetching pending GRNs");
        List<PendingGrnDTO> grns = managerService.getPendingGrns();
        return ResponseEntity.ok(grns);
    }

    // ===== STAFF SUMMARY =====

    @GetMapping("/staff/summary")
    public ResponseEntity<List<StaffSummaryDTO>> getStaffSummary() {
        log.info("Fetching staff summary");
        List<StaffSummaryDTO> staff = managerService.getStaffSummary();
        return ResponseEntity.ok(staff);
    }

    // ===== STOCK ALERTS =====

    @GetMapping("/inventory/alerts")
    public ResponseEntity<List<StockAlertDTO>> getStockAlerts() {
        log.info("Fetching stock alerts");
        List<StockAlertDTO> alerts = managerService.getStockAlerts();
        return ResponseEntity.ok(alerts);
    }

    // ===== EXPIRY ALERTS =====

    @GetMapping("/inventory/expiry-alerts")
    public ResponseEntity<List<ExpiryAlertDTO>> getExpiryAlerts() {
        log.info("Fetching expiry alerts");
        List<ExpiryAlertDTO> alerts = managerService.getExpiryAlerts();
        return ResponseEntity.ok(alerts);
    }

    // ===== BRANCH ALERTS =====

    @GetMapping("/alerts")
    public ResponseEntity<List<BranchAlertDTO>> getBranchAlerts() {
        log.info("Fetching branch alerts");
        List<BranchAlertDTO> alerts = managerService.getBranchAlerts();
        return ResponseEntity.ok(alerts);
    }

    // ===== APPROVALS =====

    @GetMapping("/approvals")
    public ResponseEntity<List<ApprovalDTO>> getApprovals(
            @RequestParam(required = false) String status) {
        log.info("Fetching approvals with status: {}", status);
        List<ApprovalDTO> approvals = managerService.getApprovals(status);
        return ResponseEntity.ok(approvals);
    }

    @PatchMapping("/approvals/{approvalId}")
    public ResponseEntity<ApprovalDTO> updateApprovalStatus(
            @PathVariable Long approvalId,
            @RequestBody ApprovalUpdateRequest request) {
        log.info("Updating approval {} to status: {}", approvalId, request.getStatus());
        ApprovalDTO approval = managerService.updateApprovalStatus(
                approvalId,
                request.getStatus(),
                request.getNotes()
        );
        return ResponseEntity.ok(approval);
    }

    // ===== BRANCH ACTIVITY LOG =====

    @GetMapping("/activity-log")
    public ResponseEntity<List<ActivityLogDTO>> getBranchActivityLog(
            @RequestParam(defaultValue = "20") int limit) {
        log.info("Fetching branch activity log with limit: {}", limit);
        List<ActivityLogDTO> activities = managerService.getBranchActivityLog(limit);
        return ResponseEntity.ok(activities);
    }
}

