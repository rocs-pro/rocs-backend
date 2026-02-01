package com.nsbm.rocs.dashboard.admin.controller;

import com.nsbm.rocs.dashboard.admin.dto.BranchDTO;
import com.nsbm.rocs.dashboard.admin.service.impl.BranchServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/branches")
public class BranchController {

    private final BranchServiceImpl branchService;

    @Autowired
    public BranchController(BranchServiceImpl branchService) {
        this.branchService = branchService;
    }

    @PostMapping("/create")
    public ResponseEntity<BranchDTO> createBranch(@RequestBody BranchDTO dto) {
        BranchDTO created = branchService.createBranch(dto);
        return ResponseEntity.status(201).body(created);
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<BranchDTO>> getAllBranches() {
        return ResponseEntity.ok(branchService.getAllBranches());
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<BranchDTO> getBranchById(@PathVariable Long id) {
        return ResponseEntity.ok(branchService.getBranchById(id));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<BranchDTO> updateBranch(@PathVariable Long id, @RequestBody BranchDTO dto) {
        return ResponseEntity.ok(branchService.updateBranch(id, dto));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteBranch(@PathVariable Long id) {
        branchService.deleteBranch(id);
        return ResponseEntity.noContent().build();
    }
}
