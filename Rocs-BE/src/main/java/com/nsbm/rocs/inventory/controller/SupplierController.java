package com.nsbm.rocs.inventory.controller;

import com.nsbm.rocs.inventory.dto.SupplierRequestDTO;
import com.nsbm.rocs.inventory.dto.SupplierResponseDTO;
import com.nsbm.rocs.inventory.service.SupplierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
        try {
            // Ensure collections are not null to prevent NullPointerException
            if (requestDTO.getContacts() == null) {
                requestDTO.setContacts(new ArrayList<>());
            }
            if (requestDTO.getBranches() == null) {
                requestDTO.setBranches(new ArrayList<>());
            }

            SupplierResponseDTO createdSupplier = supplierService.createSupplier(requestDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(InventoryResponseBuilder.build(createdSupplier, "Supplier created successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(InventoryResponseBuilder.build(null, "Error creating supplier: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateSupplier(
            @PathVariable Long id,
            @Valid @RequestBody SupplierRequestDTO requestDTO) {
        try {
            // Ensure collections are not null to prevent NullPointerException
            if (requestDTO.getContacts() == null) {
                requestDTO.setContacts(new ArrayList<>());
            }
            if (requestDTO.getBranches() == null) {
                requestDTO.setBranches(new ArrayList<>());
            }

            SupplierResponseDTO updatedSupplier = supplierService.updateSupplier(id, requestDTO);
            return ResponseEntity.ok(InventoryResponseBuilder.build(updatedSupplier, "Supplier updated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(InventoryResponseBuilder.build(null, "Error updating supplier: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteSupplier(@PathVariable Long id) {
        try {
            supplierService.deleteSupplier(id);
            return ResponseEntity.ok(InventoryResponseBuilder.buildMessage("Supplier deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(InventoryResponseBuilder.build(null, "Error deleting supplier: " + e.getMessage()));
        }
    }

    /**
     * Get active suppliers only
     */
    @GetMapping("/active")
    public ResponseEntity<Map<String, Object>> getActiveSuppliers() {
        try {
            List<SupplierResponseDTO> suppliers = supplierService.getActiveSuppliers();
            return ResponseEntity.ok(InventoryResponseBuilder.build(suppliers, "Active suppliers retrieved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(InventoryResponseBuilder.build(null, "Error retrieving suppliers: " + e.getMessage()));
        }
    }
}
