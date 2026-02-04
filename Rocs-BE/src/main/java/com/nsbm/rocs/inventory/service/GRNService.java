package com.nsbm.rocs.inventory.service;

import com.nsbm.rocs.inventory.dto.*;

import java.util.List;

public interface GRNService {

    /**
     * Create a new GRN
     */
    GRNResponseDTO createGRN(GRNCreateRequestDTO request, Long currentUserId);

    /**
     * Get GRN by ID
     */
    GRNResponseDTO getGRNById(Long grnId);

    /**
     * Get all GRNs for a branch
     */
    List<GRNResponseDTO> getGRNsByBranch(Long branchId);

    /**
     * Search GRNs with filters
     */
    List<GRNResponseDTO> searchGRNs(GRNFilterDTO filter);

    /**
     * Update GRN (only if pending)
     */
    GRNResponseDTO updateGRN(Long grnId, GRNUpdateRequestDTO request);

    /**
     * Approve GRN
     */
    GRNResponseDTO approveGRN(Long grnId, Long approvedBy);

    /**
     * Update GRN payment status
     */
    GRNResponseDTO updatePaymentStatus(Long grnId, String paymentStatus);

    /**
     * Delete GRN (only if pending)
     */
    void deleteGRN(Long grnId);

    /**
     * Get GRN statistics for a branch
     */
    GRNStatsDTO getGRNStats(Long branchId, String period);

    /**
     * Reject GRN
     */
    GRNResponseDTO rejectGRN(Long grnId, Long rejectedBy, String reason);

    /**
     * Get GRN items by product
     */
    List<GRNItemDTO> getGRNItemsByProduct(Long productId, Long branchId);

    /**
     * Check if GRN number exists
     */
    boolean isGRNNumberExists(String grnNo);
}
