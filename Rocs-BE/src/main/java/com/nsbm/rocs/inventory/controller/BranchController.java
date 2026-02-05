package com.nsbm.rocs.inventory.controller;

import com.nsbm.rocs.entity.main.Branch;
import com.nsbm.rocs.inventory.dto.BranchDTO;
import com.nsbm.rocs.repository.BranchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/inventory/branches")
@RequiredArgsConstructor
public class BranchController {

    private final BranchRepository branchRepository;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllActiveBranches() {
        try {
            List<Branch> branches = branchRepository.findByIsActiveTrue();
            List<BranchDTO> branchDTOs = branches.stream()
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(
                    InventoryResponseBuilder.build(branchDTOs, "Branches retrieved successfully")
            );
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(InventoryResponseBuilder.build(null, "Error retrieving branches: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getBranchById(@PathVariable Long id) {
        try {
            Branch branch = branchRepository.findById(id)
                    .orElse(null);

            if (branch == null) {
                return ResponseEntity.status(404)
                        .body(InventoryResponseBuilder.build(null, "Branch not found with id: " + id));
            }

            BranchDTO branchDTO = mapToDTO(branch);
            return ResponseEntity.ok(
                    InventoryResponseBuilder.build(branchDTO, "Branch retrieved successfully")
            );
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(InventoryResponseBuilder.build(null, "Error retrieving branch: " + e.getMessage()));
        }
    }

    private BranchDTO mapToDTO(Branch branch) {
        return BranchDTO.builder()
                .branchId(branch.getBranchId())
                .name(branch.getName())
                .code(branch.getCode())
                .address(branch.getAddress())
                .location(branch.getLocation())
                .phone(branch.getPhone())
                .email(branch.getEmail())
                .isActive(branch.getIsActive())
                .createdAt(branch.getCreatedAt())
                .updatedAt(branch.getUpdatedAt())
                .build();
    }
}
