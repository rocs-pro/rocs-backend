package com.nsbm.rocs.inventory.controller;

import com.nsbm.rocs.inventory.dto.SupplierRequestDTO;
import com.nsbm.rocs.inventory.dto.SupplierResponseDTO;
import com.nsbm.rocs.inventory.service.SupplierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventory/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllSuppliers() {
        List<SupplierResponseDTO> suppliers = supplierService.getAllSuppliers();
        return ResponseEntity.ok(InventoryResponseBuilder.build(suppliers, "Suppliers retrieved successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getSupplierById(@PathVariable Long id) {
        SupplierResponseDTO supplier = supplierService.getSupplierById(id);
        return ResponseEntity.ok(InventoryResponseBuilder.build(supplier, "Supplier retrieved successfully"));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createSupplier(@Valid @RequestBody SupplierRequestDTO requestDTO) {
        SupplierResponseDTO createdSupplier = supplierService.createSupplier(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(InventoryResponseBuilder.build(createdSupplier, "Supplier created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateSupplier(
            @PathVariable Long id,
            @Valid @RequestBody SupplierRequestDTO requestDTO) {
        SupplierResponseDTO updatedSupplier = supplierService.updateSupplier(id, requestDTO);
        return ResponseEntity.ok(InventoryResponseBuilder.build(updatedSupplier, "Supplier updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteSupplier(@PathVariable Long id) {
        supplierService.deleteSupplier(id);
        return ResponseEntity.ok(InventoryResponseBuilder.buildMessage("Supplier deleted successfully"));
    }
}
