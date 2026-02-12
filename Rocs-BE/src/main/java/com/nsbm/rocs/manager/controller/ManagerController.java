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
 * Base path: /api/v1/manager
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/manager")
@RequiredArgsConstructor
public class ManagerController {

    private final ManagerService managerService;
    private final com.nsbm.rocs.manager.service.JasperReportService jasperReportService;

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

    // ===== COMPREHENSIVE SALES ANALYTICS =====

    @GetMapping("/sales/analytics")
    public ResponseEntity<SalesAnalyticsDTO> getSalesAnalytics(
            @RequestParam(defaultValue = "daily") String period) {
        log.info("Fetching comprehensive sales analytics for period: {}", period);
        SalesAnalyticsDTO analytics = managerService.getSalesAnalytics(period);
        return ResponseEntity.ok(analytics);
    }

    // ===== SALES REPORTS =====

    @GetMapping("/reports/sales")
    public ResponseEntity<List<SalesReportDTO>> getSalesReports(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        log.info("Fetching sales reports from {} to {}", startDate, endDate);
        List<SalesReportDTO> reports = managerService.getSalesReports(startDate, endDate);
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/reports/sales/summary-by-terminal")
    public ResponseEntity<List<TerminalSalesDTO>> getSalesSummaryByTerminal(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        log.info("Fetching sales summary by terminal from {} to {}", startDate, endDate);
        List<TerminalSalesDTO> reports = managerService.getSalesByTerminal(startDate, endDate);
        return ResponseEntity.ok(reports);
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
                request.getNotes(),
                request.getRole()
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
    
    // ===== PDF REPORTS =====
    
    @GetMapping("/reports/sales/pdf")
    public ResponseEntity<byte[]> getSalesReportsPdf(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        try {
            byte[] pdfBytes = jasperReportService.generateSalesReportsPdf(startDate, endDate);
            return ResponseEntity.ok()
                    .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=sales_report.pdf")
                    .header(org.springframework.http.HttpHeaders.CONTENT_TYPE, org.springframework.http.MediaType.APPLICATION_PDF_VALUE)
                    .body(pdfBytes);
        } catch (Exception e) {
            log.error("Error generating sales report PDF", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/reports/activity-log/pdf")
    public ResponseEntity<byte[]> getBranchActivityLogPdf(
            @RequestParam(defaultValue = "100") int limit) {
        try {
            byte[] pdfBytes = jasperReportService.generateBranchActivityLogPdf(limit);
            return ResponseEntity.ok()
                    .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=activity_log.pdf")
                    .header(org.springframework.http.HttpHeaders.CONTENT_TYPE, org.springframework.http.MediaType.APPLICATION_PDF_VALUE)
                    .body(pdfBytes);
        } catch (Exception e) {
            log.error("Error generating activity log PDF", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/reports/loyalty/pdf")
    public ResponseEntity<byte[]> getLoyaltyCustomersPdf() {
        try {
            byte[] pdfBytes = jasperReportService.generateLoyaltyCustomersPdf();
            return ResponseEntity.ok()
                    .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=loyalty_customers.pdf")
                    .header(org.springframework.http.HttpHeaders.CONTENT_TYPE, org.springframework.http.MediaType.APPLICATION_PDF_VALUE)
                    .body(pdfBytes);
        } catch (Exception e) {
            log.error("Error generating loyalty customers PDF", e);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/reports/grns/pdf")
    public ResponseEntity<byte[]> getGrnListPdf() {
        try {
            byte[] pdfBytes = jasperReportService.generateGrnListPdf();
            return ResponseEntity.ok()
                    .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=grn_list.pdf")
                    .header(org.springframework.http.HttpHeaders.CONTENT_TYPE, org.springframework.http.MediaType.APPLICATION_PDF_VALUE)
                    .body(pdfBytes);
        } catch (Exception e) {
            log.error("Error generating GRN list PDF", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/reports/approvals/pdf")
    public ResponseEntity<byte[]> getApprovalHistoryPdf() {
        try {
            byte[] pdfBytes = jasperReportService.generateApprovalHistoryPdf();
            return ResponseEntity.ok()
                    .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=approval_history.pdf")
                    .header(org.springframework.http.HttpHeaders.CONTENT_TYPE, org.springframework.http.MediaType.APPLICATION_PDF_VALUE)
                    .body(pdfBytes);
        } catch (Exception e) {
            log.error("Error generating approval history PDF", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}

