package com.nsbm.rocs.admin.service;

import com.nsbm.rocs.admin.dto.BranchDTO;

import java.util.List;

public interface BranchService {

    BranchDTO createBranch(BranchDTO dto);

    List<BranchDTO> getAllBranches();

    BranchDTO getBranchById(Long id);

    BranchDTO updateBranch(Long id, BranchDTO dto);

    void deleteBranch(Long id);

    void toggleBranchStatus(Long id);
}
