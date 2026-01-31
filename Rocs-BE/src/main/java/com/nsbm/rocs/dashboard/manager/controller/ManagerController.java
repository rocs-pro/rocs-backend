package com.nsbm.rocs.dashboard.manager.controller;

import com.nsbm.rocs.dashboard.manager.dto.*;
import com.nsbm.rocs.dashboard.manager.service.ManagerService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for Manager Dashboard
 * Provides endpoints for all manager-related operations
 * Base path: /api/manager
 */
@RestController
@RequestMapping("/api/manager")
@RequiredArgsConstructor
public class ManagerController {

    private final ManagerService managerService;

    // Default branch ID for now (in production, get from authenticated user)
    private static final Long DEFAULT_BRANCH_ID = 1L;

    /**
     * Get dashboard statistics
     * GET /api/manager/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<List<StatCardDTO>> getDashboardStats() {
        List<StatCardDTO> stats = managerService.getDashboardStats(DEFAULT_BRANCH_ID);
        return ResponseEntity.ok(stats);
    }

    /**
     * Get sales data for charts
     * GET /api/manager/sales?period=weekly
     */
    @GetMapping("/sales")
    public ResponseEntity<List<BigDecimal>> getSalesData(
            @RequestParam(defaultValue = "weekly") String period) {
        List<BigDecimal> salesData = managerService.getSalesData(DEFAULT_BRANCH_ID, period);
        return ResponseEntity.ok(salesData);
    }

    /**
     * Get top selling products
     * GET /api/manager/products/top-selling?limit=5
     */
    @GetMapping("/products/top-selling")
    public ResponseEntity<List<TopSellingProductDTO>> getTopSellingProducts(
            @RequestParam(defaultValue = "5") int limit) {
        List<TopSellingProductDTO> products = managerService.getTopSellingProducts(DEFAULT_BRANCH_ID, limit);
        return ResponseEntity.ok(products);
    }

    /**
     * Get pending GRNs
     * GET /api/manager/grns/pending
     */
    @GetMapping("/grns/pending")
    public ResponseEntity<List<PendingGrnDTO>> getPendingGrns() {
        List<PendingGrnDTO> grns = managerService.getPendingGrns(DEFAULT_BRANCH_ID);
        return ResponseEntity.ok(grns);
    }

    /**
     * Get staff summary
     * GET /api/manager/staff/summary
     */
    @GetMapping("/staff/summary")
    public ResponseEntity<List<StaffSummaryDTO>> getStaffSummary() {
        List<StaffSummaryDTO> staff = managerService.getStaffSummary(DEFAULT_BRANCH_ID);
        return ResponseEntity.ok(staff);
    }

    /**
     * Get stock alerts
     * GET /api/manager/inventory/alerts
     */
    @GetMapping("/inventory/alerts")
    public ResponseEntity<List<StockAlertDTO>> getStockAlerts() {
        List<StockAlertDTO> alerts = managerService.getStockAlerts(DEFAULT_BRANCH_ID);
        return ResponseEntity.ok(alerts);
    }

    /**
     * Get expiry alerts
     * GET /api/manager/inventory/expiry-alerts
     */
    @GetMapping("/inventory/expiry-alerts")
    public ResponseEntity<List<ExpiryAlertDTO>> getExpiryAlerts() {
        List<ExpiryAlertDTO> alerts = managerService.getExpiryAlerts(DEFAULT_BRANCH_ID);
        return ResponseEntity.ok(alerts);
    }

    /**
     * Get branch alerts
     * GET /api/manager/alerts
     */
    @GetMapping("/alerts")
    public ResponseEntity<List<BranchAlertDTO>> getBranchAlerts() {
        List<BranchAlertDTO> alerts = managerService.getBranchAlerts(DEFAULT_BRANCH_ID);
        return ResponseEntity.ok(alerts);
    }

    /**
     * Get approvals
     * GET /api/manager/approvals?status=PENDING
     */
    @GetMapping("/approvals")
    public ResponseEntity<List<ApprovalDTO>> getApprovals(
            @RequestParam(required = false) String status) {
        List<ApprovalDTO> approvals = managerService.getApprovals(DEFAULT_BRANCH_ID, status);
        return ResponseEntity.ok(approvals);
    }

    /**
     * Update approval status
     * PATCH /api/manager/approvals/{approvalId}
     */
    @PatchMapping("/approvals/{approvalId}")
    public ResponseEntity<ApprovalDTO> updateApprovalStatus(
            @PathVariable String approvalId,
            @RequestBody Map<String, String> body) {
        String status = body.get("status");
        ApprovalDTO result = managerService.updateApprovalStatus(approvalId, status, null);
        return ResponseEntity.ok(result);
    }

    /**
     * Get branch activity log
     * GET /api/manager/activity-log?limit=20
     */
    @GetMapping("/activity-log")
    public ResponseEntity<List<ActivityLogDTO>> getBranchActivityLog(
            @RequestParam(defaultValue = "20") int limit) {
        List<ActivityLogDTO> logs = managerService.getBranchActivityLog(DEFAULT_BRANCH_ID, limit);
        return ResponseEntity.ok(logs);
    }

    /**
     * Get chart of accounts
     * GET /api/accounting/chart-of-accounts (redirect for compatibility)
     */
    @GetMapping("/chart-of-accounts")
    public ResponseEntity<List<AccountDTO>> getChartOfAccounts() {
        List<AccountDTO> accounts = managerService.getChartOfAccounts();
        return ResponseEntity.ok(accounts);
    }

    /**
     * Get journal entries
     * GET /api/accounting/journal-entries?limit=10 (redirect for compatibility)
     */
    @GetMapping("/journal-entries")
    public ResponseEntity<List<JournalEntryDTO>> getJournalEntries(
            @RequestParam(defaultValue = "10") int limit) {
        List<JournalEntryDTO> entries = managerService.getJournalEntries(DEFAULT_BRANCH_ID, limit);
        return ResponseEntity.ok(entries);
    }

    /**
     * Create journal entry
     * POST /api/accounting/journal-entries (redirect for compatibility)
     */
    @PostMapping("/journal-entries")
    public ResponseEntity<JournalEntryDTO> createJournalEntry(
            @Valid @RequestBody JournalEntryCreateDTO dto) {
        JournalEntryDTO result = managerService.createJournalEntry(DEFAULT_BRANCH_ID, dto, null);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    /**
     * Get Profit and Loss report
     * GET /api/accounting/profit-loss?period=monthly (redirect for compatibility)
     */
    @GetMapping("/profit-loss")
    public ResponseEntity<ProfitLossDTO> getProfitAndLoss(
            @RequestParam(defaultValue = "monthly") String period) {
        ProfitLossDTO pl = managerService.getProfitAndLoss(DEFAULT_BRANCH_ID, period);
        return ResponseEntity.ok(pl);
    }

    /**
     * Get sales reports
     * GET /api/reports/sales (redirect for compatibility)
     */
    @GetMapping("/sales-reports")
    public ResponseEntity<List<SalesReportDTO>> getSalesReports(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {
        List<SalesReportDTO> reports = managerService.getSalesReports(DEFAULT_BRANCH_ID, startDate, endDate);
        return ResponseEntity.ok(reports);
    }
}
