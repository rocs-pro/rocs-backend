package com.nsbm.rocs.manager.service;

import com.nsbm.rocs.entity.audit.Approval;
import com.nsbm.rocs.entity.audit.UserActivityLog;
import com.nsbm.rocs.entity.inventory.Batch;
import com.nsbm.rocs.entity.inventory.GRN;
import com.nsbm.rocs.entity.inventory.Product;
import com.nsbm.rocs.entity.inventory.Supplier;
import com.nsbm.rocs.entity.main.UserProfile;
import com.nsbm.rocs.inventory.repository.BatchRepository;
import com.nsbm.rocs.repository.GRNRepository;
import com.nsbm.rocs.inventory.repository.ProductRepository;
import com.nsbm.rocs.inventory.repository.SupplierRepository;
import com.nsbm.rocs.manager.dto.*;
import com.nsbm.rocs.manager.repository.ManagerSaleItemRepository;
import com.nsbm.rocs.manager.repository.ManagerSaleRepository;
import com.nsbm.rocs.manager.repository.ManagerUserRepository;
import com.nsbm.rocs.repository.audit.ApprovalRepository;
import com.nsbm.rocs.repository.audit.UserActivityLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ManagerService {

    private final ManagerSaleRepository saleRepository;
    private final ManagerSaleItemRepository saleItemRepository;
    private final ManagerUserRepository userRepository;
    private final GRNRepository grnRepository;
    private final ProductRepository productRepository;
    private final BatchRepository batchRepository;
    private final SupplierRepository supplierRepository;
    private final ApprovalRepository approvalRepository;
    private final UserActivityLogRepository activityLogRepository;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // ===== DASHBOARD STATS =====

    public List<DashboardStatsDTO> getDashboardStats() {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = LocalDate.now().atTime(LocalTime.MAX);
        LocalDateTime yesterdayStart = LocalDate.now().minusDays(1).atStartOfDay();
        LocalDateTime yesterdayEnd = LocalDate.now().minusDays(1).atTime(LocalTime.MAX);

        // Today's sales
        BigDecimal todaySales = saleRepository.sumNetTotalByDateRange(todayStart, todayEnd);
        Long todayTransactions = saleRepository.countByDateRange(todayStart, todayEnd);

        // Yesterday's sales for comparison
        BigDecimal yesterdaySales = saleRepository.sumNetTotalByDateRange(yesterdayStart, yesterdayEnd);

        // Pending GRNs
        List<GRN> pendingGrns = grnRepository.findByStatus("PENDING");

        // Low stock count
        List<StockAlertDTO> lowStockAlerts = getStockAlerts();

        List<DashboardStatsDTO> stats = new ArrayList<>();

        // Today's Sales stat
        stats.add(DashboardStatsDTO.builder()
                .title("Today's Sales")
                .value(formatCurrency(todaySales))
                .icon("currency")
                .tone(todaySales.compareTo(yesterdaySales) >= 0 ? "success" : "warning")
                .build());

        // Transactions stat
        stats.add(DashboardStatsDTO.builder()
                .title("Transactions")
                .value(String.valueOf(todayTransactions))
                .icon("receipt")
                .tone("info")
                .build());

        // Pending GRNs stat
        stats.add(DashboardStatsDTO.builder()
                .title("Pending GRNs")
                .value(String.valueOf(pendingGrns.size()))
                .icon("truck")
                .tone(pendingGrns.isEmpty() ? "success" : "warning")
                .build());

        // Low Stock stat
        stats.add(DashboardStatsDTO.builder()
                .title("Low Stock Items")
                .value(String.valueOf(lowStockAlerts.size()))
                .icon("package")
                .tone(lowStockAlerts.isEmpty() ? "success" : "danger")
                .build());

        return stats;
    }

    // ===== SALES DATA =====

    public List<SalesDataDTO> getSalesData(String period) {
        int days;

        switch (period.toLowerCase()) {
            case "daily":
                days = 1;
                break;
            case "monthly":
                days = 30;
                break;
            case "weekly":
            default:
                days = 7;
                break;
        }

        List<SalesDataDTO> salesData = new ArrayList<>();
        DateTimeFormatter labelFormatter = DateTimeFormatter.ofPattern("EEE");

        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime dayStart = date.atStartOfDay();
            LocalDateTime dayEnd = date.atTime(LocalTime.MAX);

            BigDecimal dailySales = saleRepository.sumNetTotalByDateRange(dayStart, dayEnd);
            Long transactions = saleRepository.countByDateRange(dayStart, dayEnd);

            salesData.add(SalesDataDTO.builder()
                    .label(date.format(labelFormatter))
                    .value(dailySales != null ? dailySales : BigDecimal.ZERO)
                    .transactions(transactions != null ? transactions.intValue() : 0)
                    .build());
        }

        return salesData;
    }

    // ===== TOP SELLING PRODUCTS =====

    public List<TopSellingProductDTO> getTopSellingProducts(int limit) {
        LocalDateTime weekStart = LocalDate.now().minusDays(7).atStartOfDay();
        LocalDateTime weekEnd = LocalDateTime.now();

        List<Object[]> results = saleItemRepository.findTopSellingProducts(weekStart, weekEnd, limit);

        return results.stream()
                .map(row -> TopSellingProductDTO.builder()
                        .productId(((Number) row[0]).longValue())
                        .name((String) row[1])
                        .sku((String) row[2])
                        .units(((Number) row[3]).intValue())
                        .revenue(formatCurrency((BigDecimal) row[4]))
                        .build())
                .collect(Collectors.toList());
    }

    // ===== PENDING GRNs =====

    public List<PendingGrnDTO> getPendingGrns() {
        List<GRN> pendingGrns = grnRepository.findByStatus("PENDING");

        return pendingGrns.stream()
                .map(grn -> {
                    String supplierName = getSupplierName(grn.getSupplierId());
                    String eta = calculateEta(grn.getGrnDate());

                    return PendingGrnDTO.builder()
                            .id(grn.getGrnNo())
                            .supplier(supplierName)
                            .items(0) // Would need GRN items count
                            .eta(eta)
                            .build();
                })
                .collect(Collectors.toList());
    }

    // ===== STAFF SUMMARY =====

    public List<StaffSummaryDTO> getStaffSummary() {
        List<UserProfile> users = userRepository.findAllActive();

        return users.stream()
                .map(user -> StaffSummaryDTO.builder()
                        .userId(user.getUserId())
                        .name(user.getFullName())
                        .role(user.getRole() != null ? user.getRole().name() : "N/A")
                        .lastLogin(formatLastLogin(user.getLastLogin()))
                        .status(determineUserStatus(user.getLastLogin()))
                        .build())
                .collect(Collectors.toList());
    }

    // ===== STOCK ALERTS =====

    public List<StockAlertDTO> getStockAlerts() {
        List<Product> products = productRepository.findByIsActiveTrue();
        List<StockAlertDTO> alerts = new ArrayList<>();

        for (Product product : products) {
            // Sum up batch quantities for this product
            List<Batch> batches = batchRepository.findByProductId(product.getProductId());
            BigDecimal totalQty = batches.stream()
                    .map(Batch::getQty)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal reorderLevel = product.getReorderLevel() != null ? product.getReorderLevel() : BigDecimal.ZERO;

            if (totalQty.compareTo(reorderLevel) <= 0) {
                String level = totalQty.compareTo(BigDecimal.ZERO) == 0 ? "Critical" : "Low";

                alerts.add(StockAlertDTO.builder()
                        .productId(product.getProductId())
                        .item(product.getName())
                        .qty(totalQty)
                        .level(level)
                        .build());
            }
        }

        return alerts;
    }

    // ===== EXPIRY ALERTS =====

    public List<ExpiryAlertDTO> getExpiryAlerts() {
        LocalDate today = LocalDate.now();
        LocalDate thirtyDaysFromNow = today.plusDays(30);

        List<Batch> expiringBatches = batchRepository.findExpiringSoonBatches(today, thirtyDaysFromNow);

        Map<Long, String> productNames = productRepository.findByIsActiveTrue().stream()
                .collect(Collectors.toMap(Product::getProductId, Product::getName));

        return expiringBatches.stream()
                .map(batch -> {
                    long daysUntilExpiry = ChronoUnit.DAYS.between(today, batch.getExpiryDate());
                    String severity = daysUntilExpiry <= 7 ? "Critical" : daysUntilExpiry <= 14 ? "Warning" : "Info";

                    return ExpiryAlertDTO.builder()
                            .batchId(batch.getBatchId())
                            .productId(batch.getProductId())
                            .item(productNames.getOrDefault(batch.getProductId(), "Unknown"))
                            .expiresOn(batch.getExpiryDate().toString())
                            .qty(batch.getQty())
                            .severity(severity)
                            .build();
                })
                .collect(Collectors.toList());
    }

    // ===== BRANCH ALERTS =====

    public List<BranchAlertDTO> getBranchAlerts() {
        List<BranchAlertDTO> alerts = new ArrayList<>();

        // Low stock alerts
        List<StockAlertDTO> stockAlerts = getStockAlerts();
        for (StockAlertDTO stockAlert : stockAlerts) {
            alerts.add(BranchAlertDTO.builder()
                    .alertId((long) alerts.size() + 1)
                    .message("Low stock: " + stockAlert.getItem() + " (" + stockAlert.getQty() + " left)")
                    .time("Now")
                    .type(stockAlert.getLevel())
                    .build());
        }

        // Expiry alerts
        List<ExpiryAlertDTO> expiryAlerts = getExpiryAlerts();
        for (ExpiryAlertDTO expiryAlert : expiryAlerts) {
            if ("Critical".equals(expiryAlert.getSeverity())) {
                alerts.add(BranchAlertDTO.builder()
                        .alertId((long) alerts.size() + 1)
                        .message("Expiring soon: " + expiryAlert.getItem() + " on " + expiryAlert.getExpiresOn())
                        .time("Now")
                        .type("Warning")
                        .build());
            }
        }

        // Pending GRN alerts
        List<PendingGrnDTO> pendingGrns = getPendingGrns();
        if (!pendingGrns.isEmpty()) {
            alerts.add(BranchAlertDTO.builder()
                    .alertId((long) alerts.size() + 1)
                    .message(pendingGrns.size() + " pending GRN(s) awaiting approval")
                    .time("Now")
                    .type("Info")
                    .build());
        }

        return alerts;
    }

    // ===== APPROVALS =====

    public List<ApprovalDTO> getApprovals(String status) {
        List<Approval> approvals;

        if (status != null && !status.isEmpty()) {
            approvals = approvalRepository.findByStatus(status.toUpperCase());
        } else {
            approvals = approvalRepository.findAll();
        }

        Map<Long, String> userNames = userRepository.findAll().stream()
                .collect(Collectors.toMap(UserProfile::getUserId, UserProfile::getFullName, (a, b) -> a));

        return approvals.stream()
                .map(approval -> ApprovalDTO.builder()
                        .id(approval.getApprovalId())
                        .category(approval.getType())
                        .reference(approval.getReferenceNo() != null ? approval.getReferenceNo() : "REF-" + approval.getReferenceId())
                        .requestedBy(userNames.getOrDefault(approval.getRequestedBy(), "Unknown"))
                        .time(formatDateTime(approval.getCreatedAt()))
                        .status(capitalizeFirst(approval.getStatus()))
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public ApprovalDTO updateApprovalStatus(Long approvalId, String status, String notes) {
        Approval approval = approvalRepository.findById(approvalId)
                .orElseThrow(() -> new RuntimeException("Approval not found: " + approvalId));

        approval.setStatus(status.toUpperCase());
        approval.setApprovalNotes(notes);
        approval.setApprovedAt(LocalDateTime.now());

        approvalRepository.save(approval);

        Map<Long, String> userNames = userRepository.findAll().stream()
                .collect(Collectors.toMap(UserProfile::getUserId, UserProfile::getFullName, (a, b) -> a));

        return ApprovalDTO.builder()
                .id(approval.getApprovalId())
                .category(approval.getType())
                .reference(approval.getReferenceNo() != null ? approval.getReferenceNo() : "REF-" + approval.getReferenceId())
                .requestedBy(userNames.getOrDefault(approval.getRequestedBy(), "Unknown"))
                .time(formatDateTime(approval.getCreatedAt()))
                .status(capitalizeFirst(approval.getStatus()))
                .build();
    }

    // ===== BRANCH ACTIVITY LOG =====

    public List<ActivityLogDTO> getBranchActivityLog(int limit) {
        List<UserActivityLog> activities = activityLogRepository.findAll(
                PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt"))
        ).getContent();

        Map<Long, String> userNames = userRepository.findAll().stream()
                .collect(Collectors.toMap(UserProfile::getUserId, UserProfile::getFullName, (a, b) -> a));

        return activities.stream()
                .map(activity -> ActivityLogDTO.builder()
                        .activityId(activity.getActivityId())
                        .time(formatDateTime(activity.getCreatedAt()))
                        .user(userNames.getOrDefault(activity.getUserId(), "System"))
                        .action(activity.getActivityType())
                        .details(activity.getDescription())
                        .severity(determineSeverity(activity.getActivityType()))
                        .build())
                .collect(Collectors.toList());
    }

    // ===== HELPER METHODS =====

    private String formatCurrency(BigDecimal amount) {
        if (amount == null) {
            amount = BigDecimal.ZERO;
        }
        return "LKR " + String.format("%,.0f", amount);
    }

    private String getSupplierName(Long supplierId) {
        if (supplierId == null) return "Unknown";
        return supplierRepository.findById(supplierId)
                .map(Supplier::getName)
                .orElse("Unknown Supplier");
    }

    private String calculateEta(LocalDate grnDate) {
        if (grnDate == null) return "Unknown";
        LocalDate today = LocalDate.now();
        if (grnDate.equals(today)) return "Today";
        if (grnDate.equals(today.plusDays(1))) return "Tomorrow";
        if (grnDate.isBefore(today)) return "Overdue";
        return grnDate.toString();
    }

    private String formatLastLogin(LocalDateTime lastLogin) {
        if (lastLogin == null) return "Never";
        return lastLogin.format(DATE_TIME_FORMATTER);
    }

    private String determineUserStatus(LocalDateTime lastLogin) {
        if (lastLogin == null) return "Inactive";
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(30);
        return lastLogin.isAfter(threshold) ? "Active" : "Offline";
    }

    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return "";
        return dateTime.format(DATE_TIME_FORMATTER);
    }

    private String capitalizeFirst(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    private String determineSeverity(String activityType) {
        if (activityType == null) return "Info";
        String upper = activityType.toUpperCase();
        if (upper.contains("DELETE") || upper.contains("ERROR") || upper.contains("FAIL")) {
            return "Critical";
        }
        if (upper.contains("UPDATE") || upper.contains("MODIFY") || upper.contains("CHANGE")) {
            return "Warning";
        }
        return "Info";
    }
}

