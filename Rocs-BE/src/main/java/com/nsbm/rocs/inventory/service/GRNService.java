package com.nsbm.rocs.inventory.service;

import com.nsbm.rocs.inventory.dto.*;

import java.util.List;

public interface GRNService {

    GRNResponseDTO createGRN(GRNCreateRequestDTO request, Long currentUserId);


    GRNResponseDTO getGRNById(Long grnId);


    List<GRNResponseDTO> getGRNsByBranch(Long branchId);


    List<GRNResponseDTO> searchGRNs(GRNFilterDTO filter);


    GRNResponseDTO updateGRN(Long grnId, GRNUpdateRequestDTO request);


    GRNResponseDTO approveGRN(Long grnId, Long approvedBy);


    GRNResponseDTO updatePaymentStatus(Long grnId, String paymentStatus);

    void deleteGRN(Long grnId);


    GRNStatsDTO getGRNStats(Long branchId, String period);

    GRNResponseDTO rejectGRN(Long grnId, Long rejectedBy, String reason);


    List<GRNItemDTO> getGRNItemsByProduct(Long productId, Long branchId);


    boolean isGRNNumberExists(String grnNo);
}
