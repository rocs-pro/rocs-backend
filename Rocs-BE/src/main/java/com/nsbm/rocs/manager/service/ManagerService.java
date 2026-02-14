package com.nsbm.rocs.manager.service;

import com.nsbm.rocs.entity.audit.Approval;
import com.nsbm.rocs.entity.audit.UserActivityLog;
import com.nsbm.rocs.entity.inventory.Batch;
import com.nsbm.rocs.entity.inventory.GRN;
import com.nsbm.rocs.entity.inventory.Product;
import com.nsbm.rocs.entity.inventory.Supplier;
import com.nsbm.rocs.entity.main.UserProfile;
import com.nsbm.rocs.entity.pos.Sale;
import com.nsbm.rocs.entity.pos.SaleItem;
import com.nsbm.rocs.entity.pos.Payment;
import com.nsbm.rocs.entity.enums.AccountStatus;
import com.nsbm.rocs.entity.enums.Role;
import com.nsbm.rocs.inventory.repository.BatchRepository;
import com.nsbm.rocs.inventory.repository.GRNRepository;
import com.nsbm.rocs.inventory.repository.ProductRepository;
import com.nsbm.rocs.inventory.repository.SupplierRepository;
import com.nsbm.rocs.manager.dto.*;
import com.nsbm.rocs.manager.repository.ManagerSaleItemRepository;
import com.nsbm.rocs.manager.repository.ManagerSaleRepository;
import com.nsbm.rocs.manager.repository.ManagerUserRepository;
import com.nsbm.rocs.repository.audit.ApprovalRepository;
import com.nsbm.rocs.repository.audit.UserActivityLogRepository;
import com.nsbm.rocs.pos.repository.CashFlowRepository;
import com.nsbm.rocs.pos.repository.PaymentRepository;
import com.nsbm.rocs.pos.repository.SalesReturnRepository;
import com.nsbm.rocs.pos.repository.SaleItemRepository;
import com.nsbm.rocs.entity.pos.CashFlow;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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
    private final CashFlowRepository cashFlowRepository;
    private final PaymentRepository paymentRepository;
    private final SalesReturnRepository salesReturnRepository;
    private final SaleItemRepository posSaleItemRepository;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

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

        // Efficiently fetch users involved in these approvals
        List<Long> userIds = approvals.stream()
                .map(Approval::getRequestedBy)
                .distinct()
                .collect(Collectors.toList());
        
        Map<Long, UserProfile> userMap = userRepository.findAllById(userIds).stream()
                .collect(Collectors.toMap(UserProfile::getUserId, u -> u));

        return approvals.stream()
                .map(approval -> mapToApprovalDTO(approval, userMap.get(approval.getRequestedBy())))
                .collect(Collectors.toList());
    }

    @Transactional
    public ApprovalDTO updateApprovalStatus(Long approvalId, String status, String notes, String role) {
        Approval approval = approvalRepository.findById(approvalId)
                .orElseThrow(() -> new RuntimeException("Approval not found: " + approvalId));

        approval.setStatus(status.toUpperCase());
        approval.setApprovalNotes(notes);
        approval.setApprovedAt(LocalDateTime.now());

        // Handle User Registration Approval logic
        UserProfile user = null;
        if ("USER_REGISTRATION".equals(approval.getType()) && "APPROVED".equalsIgnoreCase(status)) {
            user = userRepository.findById(approval.getReferenceId())
                    .orElseThrow(() -> new RuntimeException("Ref User not found: " + approval.getReferenceId()));

            user.setAccountStatus(AccountStatus.ACTIVE);
            if (role != null && !role.trim().isEmpty()) {
                try {
                    user.setRole(Role.valueOf(role));
                } catch (IllegalArgumentException e) {
                    throw new RuntimeException("Invalid role: " + role);
                }
            }
            userRepository.save(user);
        }

        // Handle Cash Flow Approval Logic
        if (approval.getType() != null && approval.getType().startsWith("CASH_FLOW_")) {
            CashFlow cashFlow = cashFlowRepository.findById(approval.getReferenceId())
                    .orElseThrow(() -> new RuntimeException("Cash Flow not found: " + approval.getReferenceId()));

            cashFlow.setStatus(status.toUpperCase()); // APPROVED, REJECTED
            cashFlowRepository.save(cashFlow);
        }

        approvalRepository.save(approval);

        return mapToApprovalDTO(approval, user);
    }

    private ApprovalDTO mapToApprovalDTO(Approval approval, UserProfile user) {
        String name = user != null ? user.getFullName() : "Unknown";
        String username = user != null ? user.getUsername() : "-";
        String email = user != null ? user.getEmail() : "-";

        BigDecimal amount = null;
        String reason = approval.getRequestNotes(); // Default to approval notes
        String type = approval.getType();
        String referenceNo = approval.getReferenceNo();

        // Check for specific reference details (e.g. CashFlow)
        if (approval.getType() != null && approval.getType().startsWith("CASH_FLOW_") && approval.getReferenceId() != null) {
            CashFlow cashFlow = cashFlowRepository.findById(approval.getReferenceId()).orElse(null);
            if (cashFlow != null) {
                amount = cashFlow.getAmount();
                reason = cashFlow.getReason(); // Prefer specific reason
                type = cashFlow.getType(); // PAID_IN or PAID_OUT
                if (cashFlow.getReferenceNo() != null && !cashFlow.getReferenceNo().isEmpty()) {
                    referenceNo = cashFlow.getReferenceNo();
                }
            }
        }

        return ApprovalDTO.builder()
            .id(approval.getApprovalId())
            .category(approval.getType())
            .reference(referenceNo != null ? referenceNo : "REF-" + approval.getReferenceId())
            .requestedBy(name)
            .username(username)
            .email(email)
            .time(formatDateTime(approval.getCreatedAt()))
            .approvedAt(formatDateTime(approval.getApprovedAt()))
            .status(capitalizeFirst(approval.getStatus()))
            .description(approval.getRequestNotes()) // Keep original notes here
            .amount(amount)
            .reason(reason)
            .type(type)
            .referenceNo(referenceNo)
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

    // ===== COMPREHENSIVE SALES ANALYTICS =====

    public SalesAnalyticsDTO getSalesAnalytics(String period) {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = LocalDate.now().atTime(LocalTime.MAX);
        LocalDateTime yesterdayStart = LocalDate.now().minusDays(1).atStartOfDay();
        LocalDateTime yesterdayEnd = LocalDate.now().minusDays(1).atTime(LocalTime.MAX);

        // Calculate date range based on period
        int days = switch (period.toLowerCase()) {
            case "daily" -> 1;
            case "monthly" -> 30;
            default -> 7; // weekly
        };
        LocalDateTime periodStart = LocalDate.now().minusDays(days - 1).atStartOfDay();

        // Basic stats
        BigDecimal todaySales = saleRepository.sumNetTotalByDateRange(todayStart, todayEnd);
        BigDecimal yesterdaySales = saleRepository.sumNetTotalByDateRange(yesterdayStart, yesterdayEnd);
        Long todayTransactions = saleRepository.countByDateRange(todayStart, todayEnd);
        Long yesterdayTransactions = saleRepository.countByDateRange(yesterdayStart, yesterdayEnd);
        Long customersServed = saleRepository.countDistinctCustomers(todayStart, todayEnd);

        todaySales = todaySales != null ? todaySales : BigDecimal.ZERO;
        yesterdaySales = yesterdaySales != null ? yesterdaySales : BigDecimal.ZERO;
        int txnCount = todayTransactions != null ? todayTransactions.intValue() : 0;

        // Calculate average transaction value
        BigDecimal avgTransaction = txnCount > 0 
            ? todaySales.divide(BigDecimal.valueOf(txnCount), 2, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;

        // Calculate growth percentage
        double growth = 0.0;
        if (yesterdaySales.compareTo(BigDecimal.ZERO) > 0) {
            growth = todaySales.subtract(yesterdaySales)
                    .divide(yesterdaySales, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .doubleValue();
        }

        // Payment breakdown
        List<PaymentBreakdownDTO> paymentBreakdown = getPaymentBreakdown(todayStart, todayEnd);

        // Hourly sales
        List<HourlySalesDTO> hourlySales = getHourlySales(LocalDateTime.now());

        // Recent transactions
        List<RecentTransactionDTO> recentTransactions = getRecentTransactions(15);

        // Top products
        List<TopSellingProductDTO> topProducts = getTopSellingProducts(5);

        // Daily trend
        List<SalesDataDTO> dailyTrend = getSalesData(period);

        return SalesAnalyticsDTO.builder()
                .todaySales(todaySales)
                .yesterdaySales(yesterdaySales)
                .weeklyAverage(calculateWeeklyAverage())
                .todayTransactions(txnCount)
                .yesterdayTransactions(yesterdayTransactions != null ? yesterdayTransactions.intValue() : 0)
                .avgTransactionValue(avgTransaction)
                .customersServed(customersServed != null ? customersServed.intValue() : 0)
                .growthPercentage(growth)
                .paymentBreakdown(paymentBreakdown)
                .hourlySales(hourlySales)
                .recentTransactions(recentTransactions)
                .topProducts(topProducts)
                .dailyTrend(dailyTrend)
                .build();
    }

    private BigDecimal calculateWeeklyAverage() {
        LocalDateTime weekStart = LocalDate.now().minusDays(6).atStartOfDay();
        LocalDateTime weekEnd = LocalDateTime.now();
        BigDecimal weekTotal = saleRepository.sumNetTotalByDateRange(weekStart, weekEnd);
        return weekTotal != null ? weekTotal.divide(BigDecimal.valueOf(7), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    private List<PaymentBreakdownDTO> getPaymentBreakdown(LocalDateTime startDate, LocalDateTime endDate) {
        List<Object[]> results = paymentRepository.findPaymentBreakdownByDateRange(startDate, endDate);
        BigDecimal total = results.stream()
                .map(row -> (BigDecimal) row[1])
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return results.stream()
                .map(row -> {
                    String method = (String) row[0];
                    BigDecimal amount = (BigDecimal) row[1];
                    int count = ((Number) row[2]).intValue();
                    double percentage = total.compareTo(BigDecimal.ZERO) > 0
                            ? amount.divide(total, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue()
                            : 0.0;

                    return PaymentBreakdownDTO.builder()
                            .method(method != null ? method : "OTHER")
                            .amount(amount)
                            .count(count)
                            .percentage(percentage)
                            .build();
                })
                .collect(Collectors.toList());
    }

    private List<HourlySalesDTO> getHourlySales(LocalDateTime targetDate) {
        List<Object[]> results = saleRepository.findHourlySales(targetDate);
        
        // Create a map for quick lookup
        Map<Integer, Object[]> hourlyMap = results.stream()
                .collect(Collectors.toMap(
                        row -> ((Number) row[0]).intValue(),
                        row -> row
                ));

        // Build full 24-hour list
        List<HourlySalesDTO> hourlyList = new ArrayList<>();
        for (int hour = 0; hour < 24; hour++) {
            Object[] data = hourlyMap.get(hour);
            String hourLabel = String.format("%02d:00", hour);
            
            if (data != null) {
                hourlyList.add(HourlySalesDTO.builder()
                        .hour(hourLabel)
                        .sales((BigDecimal) data[1])
                        .transactions(((Number) data[2]).intValue())
                        .build());
            } else {
                hourlyList.add(HourlySalesDTO.builder()
                        .hour(hourLabel)
                        .sales(BigDecimal.ZERO)
                        .transactions(0)
                        .build());
            }
        }
        return hourlyList;
    }

    private List<RecentTransactionDTO> getRecentTransactions(int limit) {
        List<Sale> recentSales = saleRepository.findRecentSales(PageRequest.of(0, limit));
        
        Map<Long, String> userNames = userRepository.findAll().stream()
                .collect(Collectors.toMap(UserProfile::getUserId, UserProfile::getFullName, (a, b) -> a));

        return recentSales.stream()
                .map(sale -> {
                    // Get item count
                    List<SaleItem> items = posSaleItemRepository.findBySaleId(sale.getSaleId());
                    int itemCount = items != null ? items.size() : 0;
                    
                    // Get payment method
                    List<Payment> payments = paymentRepository.findBySaleId(sale.getSaleId());
                    String paymentMethod = payments != null && !payments.isEmpty() 
                            ? payments.get(0).getPaymentType() 
                            : "CASH";

                    return RecentTransactionDTO.builder()
                            .saleId(sale.getSaleId())
                            .invoiceNo(sale.getInvoiceNo())
                            .cashier(userNames.getOrDefault(sale.getCashierId(), "Unknown"))
                            .itemCount(itemCount)
                            .amount(sale.getNetTotal())
                            .paymentMethod(paymentMethod)
                            .type("SALE")
                            .time(sale.getSaleDate().format(TIME_FORMATTER))
                            .date(sale.getSaleDate().format(DATE_FORMATTER))
                            .build();
                })
                .collect(Collectors.toList());
    }

    // ===== SALES REPORTS =====

    public List<SalesReportDTO> getSalesReports(String startDateStr, String endDateStr) {
        LocalDate startDate = startDateStr != null 
                ? LocalDate.parse(startDateStr) 
                : LocalDate.now().minusDays(7);
        LocalDate endDate = endDateStr != null 
                ? LocalDate.parse(endDateStr) 
                : LocalDate.now();

        List<SalesReportDTO> reports = new ArrayList<>();

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            LocalDateTime dayStart = date.atStartOfDay();
            LocalDateTime dayEnd = date.atTime(LocalTime.MAX);

            // Sales totals
            BigDecimal revenue = saleRepository.sumNetTotalByDateRange(dayStart, dayEnd);
            BigDecimal grossTotal = saleRepository.sumGrossTotalByDateRange(dayStart, dayEnd);
            Long invoiceCount = saleRepository.countByDateRange(dayStart, dayEnd);

            revenue = revenue != null ? revenue : BigDecimal.ZERO;
            grossTotal = grossTotal != null ? grossTotal : BigDecimal.ZERO;
            int invoices = invoiceCount != null ? invoiceCount.intValue() : 0;

            // Payment breakdown
            BigDecimal cashSales = paymentRepository.sumByTypeAndDateRange(dayStart, dayEnd, "CASH");
            BigDecimal cardSales = paymentRepository.sumByTypeAndDateRange(dayStart, dayEnd, "CARD");
            BigDecimal qrSales = paymentRepository.sumByTypeAndDateRange(dayStart, dayEnd, "QR");

            cashSales = cashSales != null ? cashSales : BigDecimal.ZERO;
            cardSales = cardSales != null ? cardSales : BigDecimal.ZERO;
            qrSales = qrSales != null ? qrSales : BigDecimal.ZERO;

            // Returns
            BigDecimal returns = salesReturnRepository.sumTotalAmountByDateRange(dayStart, dayEnd);
            returns = returns != null ? returns : BigDecimal.ZERO;

            // Calculate cost (estimate as 70% of revenue for now - can be replaced with actual COGS)
            BigDecimal cost = revenue.multiply(BigDecimal.valueOf(0.70)).setScale(2, RoundingMode.HALF_UP);
            BigDecimal profit = revenue.subtract(cost);

            // Average basket size
            BigDecimal avgBasket = invoices > 0 
                    ? revenue.divide(BigDecimal.valueOf(invoices), 2, RoundingMode.HALF_UP) 
                    : BigDecimal.ZERO;

            // Profit margin
            double profitMargin = revenue.compareTo(BigDecimal.ZERO) > 0
                    ? profit.divide(revenue, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue()
                    : 0.0;

            reports.add(SalesReportDTO.builder()
                    .date(date.format(DATE_FORMATTER))
                    .dayName(date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH))
                    .invoices(invoices)
                    .revenue(revenue)
                    .cost(cost)
                    .profit(profit)
                    .cashSales(cashSales)
                    .cardSales(cardSales)
                    .qrSales(qrSales)
                    .returns(returns)
                    .avgBasket(avgBasket)
                    .profitMargin(profitMargin)
                    .build());
        }

        return reports;
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

    // ===== TERMINAL SALES REPORTS =====
    
    public List<TerminalSalesDTO> getSalesByTerminal(String startDateStr, String endDateStr) {
        LocalDate startDate = startDateStr != null 
                ? LocalDate.parse(startDateStr) 
                : LocalDate.now().minusDays(7);
        LocalDate endDate = endDateStr != null 
                ? LocalDate.parse(endDateStr) 
                : LocalDate.now();

        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);

        List<Object[]> revenueData = saleRepository.findRevenueByTerminal(start, end);
        List<Object[]> paymentData = saleRepository.findPaymentsByTerminal(start, end);

        BigDecimal totalRevenueAll = revenueData.stream()
            .map(row -> (BigDecimal) row[2])
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Process payments per terminal
        // Map<TerminalId, Map<Type, Amount>>
        Map<Long, Map<String, BigDecimal>> paymentsMap = paymentData.stream()
            .filter(row -> row[1] != null)
            .collect(Collectors.groupingBy(
                row -> (Long) row[0],
                Collectors.toMap(
                    row -> (String) row[1],
                    row -> (BigDecimal) row[2],
                    BigDecimal::add
                )
            ));

        return revenueData.stream().map(row -> {
            Long terminalId = (Long) row[0];
            Long count = (Long) row[1];
            BigDecimal revenue = (BigDecimal) row[2];
            
            Map<String, BigDecimal> pMap = paymentsMap.getOrDefault(terminalId, Map.of());
            BigDecimal cash = pMap.getOrDefault("CASH", BigDecimal.ZERO);
            BigDecimal card = pMap.getOrDefault("CARD", BigDecimal.ZERO);

            return TerminalSalesDTO.builder()
                .id(terminalId)
                .name("Terminal #" + terminalId)
                .code("T-" + String.format("%02d", terminalId))
                .revenue(revenue)
                .transactionCount(count.intValue())
                .cashSales(cash)
                .cardSales(card)
                .totalRevenue(totalRevenueAll)
                .build();
        }).collect(Collectors.toList());
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
