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
}   