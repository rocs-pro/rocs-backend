package com.nsbm.rocs.dashboard.admin.service;

import com.nsbm.rocs.dashboard.admin.dto.BranchDTO;

import java.util.List;

public interface BranchService {

    BranchDTO createBranch(BranchDTO dto);

    List<BranchDTO> getAllBranches();

    BranchDTO getBranchById(Long id);

    BranchDTO updateBranch(Long id, BranchDTO dto);

    void deleteBranch(Long id);
}
