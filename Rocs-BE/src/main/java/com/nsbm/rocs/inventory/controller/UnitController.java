package com.nsbm.rocs.inventory.controller;

import com.nsbm.rocs.inventory.dto.UnitDTO;
import com.nsbm.rocs.inventory.service.UnitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        return ResponseEntity.ok(InventoryResponseBuilder.build(units, "Units retrieved successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getUnitById(@PathVariable Long id) {
        UnitDTO unit = unitService.getUnitById(id);
        return ResponseEntity.ok(InventoryResponseBuilder.build(unit, "Unit retrieved successfully"));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createUnit(@Valid @RequestBody UnitDTO unitDTO) {
        UnitDTO createdUnit = unitService.createUnit(unitDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(InventoryResponseBuilder.build(createdUnit, "Unit created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateUnit(
            @PathVariable Long id,
            @Valid @RequestBody UnitDTO unitDTO) {
        UnitDTO updatedUnit = unitService.updateUnit(id, unitDTO);
        return ResponseEntity.ok(InventoryResponseBuilder.build(updatedUnit, "Unit updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteUnit(@PathVariable Long id) {
        unitService.deleteUnit(id);
        return ResponseEntity.ok(InventoryResponseBuilder.buildMessage("Unit deleted successfully"));
    }
}
