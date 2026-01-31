package com.nsbm.rocs.dashboard.manager.controller;

import com.nsbm.rocs.dashboard.manager.dto.*;
import com.nsbm.rocs.dashboard.manager.service.ManagerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Accounting operations
 * Maps to /api/accounting/* as expected by frontend
 */
@RestController
@RequestMapping("/api/accounting")
@RequiredArgsConstructor
public class AccountingController {

    private final ManagerService managerService;
    private static final Long DEFAULT_BRANCH_ID = 1L;

    /**
     * Get chart of accounts
     * GET /api/accounting/chart-of-accounts
     */
    @GetMapping("/chart-of-accounts")
    public ResponseEntity<List<AccountDTO>> getChartOfAccounts() {
        List<AccountDTO> accounts = managerService.getChartOfAccounts();
        return ResponseEntity.ok(accounts);
    }

    /**
     * Get journal entries
     * GET /api/accounting/journal-entries?limit=10
     */
    @GetMapping("/journal-entries")
    public ResponseEntity<List<JournalEntryDTO>> getJournalEntries(
            @RequestParam(defaultValue = "10") int limit) {
        List<JournalEntryDTO> entries = managerService.getJournalEntries(DEFAULT_BRANCH_ID, limit);
        return ResponseEntity.ok(entries);
    }

    /**
     * Create journal entry
     * POST /api/accounting/journal-entries
     */
    @PostMapping("/journal-entries")
    public ResponseEntity<JournalEntryDTO> createJournalEntry(
            @Valid @RequestBody JournalEntryCreateDTO dto) {
        JournalEntryDTO result = managerService.createJournalEntry(DEFAULT_BRANCH_ID, dto, null);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    /**
     * Get Profit and Loss report
     * GET /api/accounting/profit-loss?period=monthly
     */
    @GetMapping("/profit-loss")
    public ResponseEntity<ProfitLossDTO> getProfitAndLoss(
            @RequestParam(defaultValue = "monthly") String period) {
        ProfitLossDTO pl = managerService.getProfitAndLoss(DEFAULT_BRANCH_ID, period);
        return ResponseEntity.ok(pl);
    }
}

