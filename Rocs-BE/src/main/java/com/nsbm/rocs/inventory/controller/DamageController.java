package com.nsbm.rocs.inventory.controller;

import com.nsbm.rocs.inventory.dto.DamagedProductDTO;
import com.nsbm.rocs.inventory.service.DamageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventory/damages")
@RequiredArgsConstructor
public class DamageController {

    private final DamageService damageService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllDamages(
            @RequestParam(required = false) Long branchId,
            @RequestParam(required = false) Long productId) {
        List<DamagedProductDTO> damages = damageService.getAllDamages(branchId, productId);
        return ResponseEntity.ok(InventoryResponseBuilder.build(damages, "Damages retrieved successfully"));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createDamage(@Valid @RequestBody DamagedProductDTO damageDTO) {
        DamagedProductDTO created = damageService.createDamage(damageDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(InventoryResponseBuilder.build(created, "Damage entry created successfully"));
    }
}

