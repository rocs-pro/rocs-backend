package com.nsbm.rocs.pos.controller;

import com.nsbm.rocs.entity.pos.CashShift;
import com.nsbm.rocs.pos.dto.ShiftStartRequest;
import com.nsbm.rocs.pos.dto.shift.CloseShiftRequest;
import com.nsbm.rocs.pos.service.ShiftService;
import com.nsbm.rocs.common.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/pos")
@CrossOrigin // Added to fix CORS issues
public class ShiftController {

    @Autowired
    private ShiftService shiftService;

    @PostMapping("/shift/open")
    public ResponseEntity<?> startShift(@Valid @RequestBody ShiftStartRequest request) {
        try {
            Long shiftId = shiftService.startShift(request);
            return ResponseEntity.ok(Map.of("shiftId", shiftId, "status", "Shift opened successfully"));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            // Catch RuntimeExceptions (like "Invalid supervisor credentials") as 400/401
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Internal Server Error: " + e.getMessage()));
        }
    }

    @GetMapping("/shift/active")
    public ResponseEntity<ApiResponse<CashShift>> getActiveShift(@RequestParam Long terminalId) {
        try {
            CashShift activeShift = shiftService.getActiveShiftByTerminalId(terminalId);
            if (activeShift == null) {
                 return ResponseEntity.status(404).body(ApiResponse.error("No active shift found for terminal " + terminalId));
            }
            return ResponseEntity.ok(ApiResponse.success("Active shift found", activeShift));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping({"/shift/{shiftId}/totals", "/shifts/{shiftId}/totals"})
    public ResponseEntity<ApiResponse<Map<String, Object>>> getShiftTotals(@PathVariable Long shiftId) {
        try {
            Map<String, Object> totals = shiftService.getShiftTotals(shiftId);
            return ResponseEntity.ok(ApiResponse.success("Shift totals", totals));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/shift/close")
    public ResponseEntity<ApiResponse<String>> closeShift(@RequestBody CloseShiftRequest request) {
        // TODO: In production, extract this ID from the SecurityContext
        Long cashierId = 1001L;

        try {
            shiftService.closeShift(cashierId, request);
            return ResponseEntity.ok(ApiResponse.success("Shift closed successfully", "Shift closed"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("/shift/{shiftId}/close")
    public ResponseEntity<ApiResponse<String>> closeShiftById(@PathVariable Long shiftId, @RequestBody CloseShiftRequest request) {
        try {
            shiftService.closeShiftById(shiftId, request);
            return ResponseEntity.ok(ApiResponse.success("Shift closed successfully", "Shift closed"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/cashiers")
    public ResponseEntity<ApiResponse<java.util.List<com.nsbm.rocs.entity.main.UserProfile>>> getCashiers(@RequestParam Long branchId) {
        try {
            java.util.List<com.nsbm.rocs.entity.main.UserProfile> cashiers = shiftService.getCashiersByBranch(branchId);
            return ResponseEntity.ok(ApiResponse.success("Cashiers retrieved", cashiers));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
