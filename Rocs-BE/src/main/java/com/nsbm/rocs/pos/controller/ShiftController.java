package com.nsbm.rocs.pos.controller;

import com.nsbm.rocs.pos.dto.ShiftStartRequest;
import com.nsbm.rocs.pos.service.ShiftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/pos/shift")
public class ShiftController {

    @Autowired
    private ShiftService shiftService;

    @PostMapping("/open")
    public ResponseEntity<?> startShift(@RequestBody ShiftStartRequest request) {
        try {
            Long shiftId = shiftService.startShift(request);
            return ResponseEntity.ok(Map.of("shiftId", shiftId, "status", "Shift opened successfully"));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Internal Server Error: " + e.getMessage()));
        }
    }
}
