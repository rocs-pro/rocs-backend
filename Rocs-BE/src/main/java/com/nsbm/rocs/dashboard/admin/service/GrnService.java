package com.nsbm.rocs.dashboard.admin.service;

import com.nsbm.rocs.dashboard.admin.dto.GrnDTO;

import java.util.List;

public interface GrnService {
    /**
     * Return the count of pending GRNs for the given branch.
     */
    Long getPendingGrnCount(Long branchId);

    /**
     * Return the total count of pending GRNs across all branches.
     */
    Long getPendingGrnCountAll();

    /**
     * Return all pending GRNs across all branches mapped to GrnDTO.
     */
    List<GrnDTO> getAllPendingGrns();
}
