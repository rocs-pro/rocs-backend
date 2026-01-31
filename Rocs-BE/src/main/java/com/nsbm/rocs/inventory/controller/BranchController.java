package com.nsbm.rocs.inventory.controller;

import com.nsbm.rocs.auth.repo.BranchRepo;
import com.nsbm.rocs.entity.main.Branch;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/public/branches")
@RequiredArgsConstructor
public class BranchController {

    private final BranchRepo branchRepo;

    @GetMapping
    public List<Branch> getAllBranches() {
        return branchRepo.findAll();
    }
}
