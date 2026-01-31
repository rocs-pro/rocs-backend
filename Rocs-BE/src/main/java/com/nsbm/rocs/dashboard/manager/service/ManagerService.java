package com.nsbm.rocs.dashboard.manager.service;

import com.nsbm.rocs.dashboard.manager.dto.*;
import com.nsbm.rocs.dashboard.manager.repository.ApprovalRepository;
import com.nsbm.rocs.entity.main.Approval;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Service layer for Manager Dashboard operations
 * Provides data for the manager dashboard frontend
 * Now integrated with actual database repositories
 */
@Service
@RequiredArgsConstructor
public class ManagerService {

    private final ApprovalRepository approvalRepository;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Get dashboard statistics for a branch
     */
    public List<StatCardDTO> getDashboardStats(Long branchId) {
        // TODO: Implement actual database query
        return new ArrayList<>();
    }

    /**
     * Get sales data for charts
     */
    public List<BigDecimal> getSalesData(Long branchId, String period) {
        // TODO: Implement actual database query
        return new ArrayList<>();
    }

    /**
     * Get top selling products for a branch
     */
    public List<TopSellingProductDTO> getTopSellingProducts(Long branchId, int limit) {
        // TODO: Implement actual database query
        return new ArrayList<>();
    }

    /**
     * Get pending GRNs for a branch
     */
    public List<PendingGrnDTO> getPendingGrns(Long branchId) {
        // TODO: Implement actual database query
        return new ArrayList<>();
    }

    /**
     * Get staff summary for a branch
     */
    public List<StaffSummaryDTO> getStaffSummary(Long branchId) {
        // TODO: Implement actual database query
        return new ArrayList<>();
    }

    /**
     * Get stock alerts for a branch
     */
    public List<StockAlertDTO> getStockAlerts(Long branchId) {
        // TODO: Implement actual database query
        return new ArrayList<>();
    }

    /**
     * Get expiry alerts for a branch
     */
    public List<ExpiryAlertDTO> getExpiryAlerts(Long branchId) {
        // TODO: Implement actual database query
        return new ArrayList<>();
    }

    /**
     * Get branch alerts
     */
    public List<BranchAlertDTO> getBranchAlerts(Long branchId) {
        List<BranchAlertDTO> alerts = new ArrayList<>();
        alerts.add(BranchAlertDTO.builder().type("Info").message("Cashier shift started: John").time("2026-01-09 09:12").build());
        alerts.add(BranchAlertDTO.builder().type("Warning").message("Low stock: Milk Powder 1kg").time("2026-01-09 09:40").build());
        alerts.add(BranchAlertDTO.builder().type("Critical").message("Expiry today: Bread").time("2026-01-09 07:55").build());
        return alerts;
    }

    /**
     * Get approvals for a branch
     */
    public List<ApprovalDTO> getApprovals(Long branchId, String status) {
        List<Approval> approvals;

        if (status != null && !status.isEmpty()) {
            // Convert frontend status format to database format
            String dbStatus = status.toUpperCase();
            approvals = approvalRepository.findByBranchIdAndStatusOrderByCreatedAtDesc(branchId, dbStatus);
        } else {
            approvals = approvalRepository.findByBranchIdOrderByCreatedAtDesc(branchId);
        }

        // Convert entities to DTOs with formatted IDs
        return approvals.stream()
                .map(this::convertToApprovalDTO)
                .toList();
    }

    /**
     * Update approval status
     */
    public ApprovalDTO updateApprovalStatus(String approvalId, String status, Long approvedById) {
        // Parse the approval ID (format: APR-{id})
        Long id = parseApprovalId(approvalId);

        Approval approval = approvalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Approval not found: " + approvalId));

        // Update status
        approval.setStatus(status.toUpperCase());
        approval.setApprovedBy(approvedById != null ? approvedById : 1L); // Default to admin if not provided
        approval.setApprovedAt(LocalDateTime.now());

        // Save to database
        approval = approvalRepository.save(approval);

        return convertToApprovalDTO(approval);
    }

    /**
     * Convert Approval entity to DTO with formatted ID
     */
    private ApprovalDTO convertToApprovalDTO(Approval approval) {
        return ApprovalDTO.builder()
                .id(formatApprovalId(approval.getApprovalId()))
                .category(approval.getType())
                .reference(approval.getReferenceNo() != null ? approval.getReferenceNo() : "N/A")
                .requestedBy("User-" + approval.getRequestedBy()) // In production, fetch actual user name
                .time(approval.getCreatedAt().format(DATE_TIME_FORMATTER))
                .status(approval.getStatus())
                .build();
    }

    /**
     * Format approval ID as APR-{id}
     */
    private String formatApprovalId(Long id) {
        return String.format("APR-%04d", id);
    }

    /**
     * Parse approval ID from format APR-{id} to Long
     */
    private Long parseApprovalId(String approvalId) {
        try {
            // Handle format APR-0001 or APR-1
            String numericPart = approvalId.replace("APR-", "").trim();
            return Long.parseLong(numericPart);
        } catch (Exception e) {
            throw new RuntimeException("Invalid approval ID format: " + approvalId);
        }
    }

    /**
     * Get branch activity log
     */
    public List<ActivityLogDTO> getBranchActivityLog(Long branchId, int limit) {
        List<ActivityLogDTO> logs = new ArrayList<>();
        logs.add(ActivityLogDTO.builder().time("2026-01-09 09:12").user("John").action("Login").details("Cashier logged in").severity("Info").build());
        logs.add(ActivityLogDTO.builder().time("2026-01-09 09:30").user("Anne").action("Login").details("Assistant Manager logged in").severity("Info").build());
        logs.add(ActivityLogDTO.builder().time("2026-01-09 10:18").user("Saman").action("GRN Request").details("Created GRN-1021").severity("Warning").build());
        logs.add(ActivityLogDTO.builder().time("2026-01-09 10:55").user("John").action("Price Override").details("Requested override for INV-88912").severity("Warning").build());
        return logs.subList(0, Math.min(limit, logs.size()));
    }

    /**
     * Get chart of accounts
     */
    public List<AccountDTO> getChartOfAccounts() {
        List<AccountDTO> accounts = new ArrayList<>();
        accounts.add(AccountDTO.builder().code("1000").name("Cash").type("Asset").build());
        accounts.add(AccountDTO.builder().code("1100").name("Accounts Receivable").type("Asset").build());
        accounts.add(AccountDTO.builder().code("2000").name("Accounts Payable").type("Liability").build());
        accounts.add(AccountDTO.builder().code("3000").name("Owner's Equity").type("Equity").build());
        accounts.add(AccountDTO.builder().code("4000").name("Sales Revenue").type("Revenue").build());
        accounts.add(AccountDTO.builder().code("5000").name("Cost of Goods Sold").type("Expense").build());
        accounts.add(AccountDTO.builder().code("5100").name("Salaries Expense").type("Expense").build());
        return accounts;
    }

    /**
     * Get journal entries
     */
    public List<JournalEntryDTO> getJournalEntries(Long branchId, int limit) {
        List<JournalEntryDTO> entries = new ArrayList<>();
        entries.add(JournalEntryDTO.builder()
                .id("JE-0011")
                .date("2026-01-09")
                .description("Cash sales (POS)")
                .lines(List.of(
                        JournalEntryDTO.LineDTO.builder().account("Cash").dr(new BigDecimal("214600")).cr(BigDecimal.ZERO).build(),
                        JournalEntryDTO.LineDTO.builder().account("Sales Revenue").dr(BigDecimal.ZERO).cr(new BigDecimal("214600")).build()
                ))
                .build());
        return entries;
    }

    /**
     * Create journal entry
     */
    public JournalEntryDTO createJournalEntry(Long branchId, JournalEntryCreateDTO dto, Long createdById) {
        // In production, this would save to database
        return JournalEntryDTO.builder()
                .id("JE-0012")
                .date(dto.getDate().format(DATE_FORMATTER))
                .description(dto.getDescription())
                .lines(dto.getLines().stream()
                        .map(l -> JournalEntryDTO.LineDTO.builder()
                                .account(l.getAccount())
                                .dr(l.getDr())
                                .cr(l.getCr())
                                .build())
                        .toList())
                .build();
    }

    /**
     * Get Profit and Loss report
     */
    public ProfitLossDTO getProfitAndLoss(Long branchId, String period) {
        String periodLabel = switch (period.toLowerCase()) {
            case "daily" -> LocalDate.now().format(DATE_FORMATTER);
            case "weekly" -> "Last 7 days";
            case "quarterly" -> "Last Quarter";
            case "yearly" -> "Last 12 months";
            default -> LocalDate.now().getMonth().name() + " " + LocalDate.now().getYear() + " (MTD)";
        };

        return ProfitLossDTO.builder()
                .period(periodLabel)
                .revenue(new BigDecimal("1250000"))
                .cogs(new BigDecimal("740000"))
                .grossProfit(new BigDecimal("510000"))
                .expenses(List.of(
                        new ProfitLossDTO.ExpenseDTO("Salaries", new BigDecimal("180000")),
                        new ProfitLossDTO.ExpenseDTO("Electricity", new BigDecimal("45000")),
                        new ProfitLossDTO.ExpenseDTO("Transport", new BigDecimal("35000")),
                        new ProfitLossDTO.ExpenseDTO("Rent", new BigDecimal("60000"))
                ))
                .build();
    }

    /**
     * Get sales reports
     */
    public List<SalesReportDTO> getSalesReports(Long branchId, LocalDate startDate, LocalDate endDate) {
        List<SalesReportDTO> reports = new ArrayList<>();
        reports.add(SalesReportDTO.builder().date("2026-01-03").invoices(182).revenue(new BigDecimal("198000")).profit(new BigDecimal("41000")).build());
        reports.add(SalesReportDTO.builder().date("2026-01-04").invoices(205).revenue(new BigDecimal("221500")).profit(new BigDecimal("46200")).build());
        reports.add(SalesReportDTO.builder().date("2026-01-05").invoices(164).revenue(new BigDecimal("175900")).profit(new BigDecimal("35200")).build());
        reports.add(SalesReportDTO.builder().date("2026-01-06").invoices(240).revenue(new BigDecimal("268400")).profit(new BigDecimal("59800")).build());
        reports.add(SalesReportDTO.builder().date("2026-01-07").invoices(231).revenue(new BigDecimal("251100")).profit(new BigDecimal("53100")).build());
        return reports;
    }
}

