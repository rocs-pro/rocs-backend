package com.nsbm.rocs.inventory.controller;

import com.nsbm.rocs.inventory.dto.UnitDTO;
import com.nsbm.rocs.inventory.service.UnitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventory/units")
@RequiredArgsConstructor
public class UnitController {

    private final UnitService unitService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllUnits() {
        List<UnitDTO> units = unitService.getAllUnits();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", units);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getUnitById(@PathVariable Long id) {
        UnitDTO unit = unitService.getUnitById(id);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", unit);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createUnit(@Valid @RequestBody UnitDTO unitDTO) {
        UnitDTO createdUnit = unitService.createUnit(unitDTO);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Unit created successfully");
        response.put("data", createdUnit);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateUnit(
            @PathVariable Long id,
            @Valid @RequestBody UnitDTO unitDTO) {
        UnitDTO updatedUnit = unitService.updateUnit(id, unitDTO);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Unit updated successfully");
        response.put("data", updatedUnit);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteUnit(@PathVariable Long id) {
        unitService.deleteUnit(id);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Unit deleted successfully");
        return ResponseEntity.ok(response);
    }
}

