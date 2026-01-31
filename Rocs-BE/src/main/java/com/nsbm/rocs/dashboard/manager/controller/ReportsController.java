package com.nsbm.rocs.dashboard.manager.controller;

import com.nsbm.rocs.dashboard.manager.dto.SalesReportDTO;
import com.nsbm.rocs.dashboard.manager.service.ManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * REST Controller for Reports
 * Maps to /api/reports/* as expected by frontend
 */
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportsController {

    private final ManagerService managerService;
    private static final Long DEFAULT_BRANCH_ID = 1L;

    /**
     * Get sales reports
     * GET /api/reports/sales?startDate=2026-01-01&endDate=2026-01-31
     */
    @GetMapping("/sales")
    public ResponseEntity<List<SalesReportDTO>> getSalesReports(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<SalesReportDTO> reports = managerService.getSalesReports(DEFAULT_BRANCH_ID, startDate, endDate);
        return ResponseEntity.ok(reports);
    }
}

