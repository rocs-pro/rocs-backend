package com.nsbm.rocs.pos.controller;

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
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Internal Server Error: " + e.getMessage()));
        }
    }

    // Support both singular and plural content structure if needed, or just specific mapping
    @GetMapping({"/shift/{shiftId}/totals", "/shifts/{shiftId}/totals"})
    public ResponseEntity<ApiResponse<Map<String, Object>>> getShiftTotals(@PathVariable Long shiftId) {
        Map<String, Object> totals = shiftService.getShiftTotals(shiftId);
        return ResponseEntity.ok(ApiResponse.success("Shift totals", totals));
    }

    @PostMapping("/shift/close")
    public ResponseEntity<ApiResponse<String>> closeShift(@RequestBody CloseShiftRequest request) {
        // Assume sending active cashier ID in request or via context.
        // For security, get logged in user.
        // But for this specific requirement, we might need shiftId explicitly if not inferred.
        // Assuming current user's open shift.

        Long cashierId = 1001L; // logged in user ID placeholder

        try {
            shiftService.closeShift(cashierId, request);
            return ResponseEntity.ok(ApiResponse.success("Shift closed successfully", "Shift closed"));
        } catch (Exception e) {
             return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
