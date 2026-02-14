package com.nsbm.rocs.inventory.service;

import com.nsbm.rocs.inventory.dto.GRNPaymentRequestDTO;
import com.nsbm.rocs.inventory.dto.TransferToManagerRequest;
import com.nsbm.rocs.inventory.dto.ProcessPaymentRequest;

import java.util.List;

public interface GRNPaymentRequestService {
    
    /**
     * Create a payment request when GRN is approved
     */
    GRNPaymentRequestDTO createPaymentRequest(Long grnId, Long requestedBy);
    
    /**
     * Get all payment requests for a branch (for POS display)
     */
    List<GRNPaymentRequestDTO> getPaymentRequestsByBranch(Long branchId);
    
    /**
     * Get pending payment requests count for POS notification
     */
    Long getPendingCountByBranch(Long branchId);
    
    /**
     * Get payment requests with specific status
     */
    List<GRNPaymentRequestDTO> getPaymentRequestsByStatus(String status);
    
    /**
     * Get payment requests for manager (transferred ones)
     */
    List<GRNPaymentRequestDTO> getManagerPaymentRequests();
    
    /**
     * Get manager pending count for notification
     */
    Long getManagerPendingCount();
    
    /**
     * Transfer payment request to manager with supervisor approval
     */
    GRNPaymentRequestDTO transferToManager(Long requestId, TransferToManagerRequest request, Long transferredBy);
    
    /**
     * Manager processes the payment
     */
    GRNPaymentRequestDTO processPayment(Long requestId, ProcessPaymentRequest request, Long processedBy);
    
    /**
     * Reject a payment request
     */
    GRNPaymentRequestDTO rejectRequest(Long requestId, String reason, Long rejectedBy);
    
    /**
     * Get single payment request by ID
     */
    GRNPaymentRequestDTO getPaymentRequestById(Long requestId);
}
