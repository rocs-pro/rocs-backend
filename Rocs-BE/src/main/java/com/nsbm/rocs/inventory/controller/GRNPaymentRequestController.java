package com.nsbm.rocs.inventory.controller;

import com.nsbm.rocs.common.response.ApiResponse;
import com.nsbm.rocs.entity.main.UserProfile;
import com.nsbm.rocs.inventory.dto.GRNPaymentRequestDTO;
import com.nsbm.rocs.inventory.dto.ProcessPaymentRequest;
import com.nsbm.rocs.inventory.dto.TransferToManagerRequest;
import com.nsbm.rocs.inventory.service.GRNPaymentRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/grn-payments")
@CrossOrigin
@RequiredArgsConstructor
public class GRNPaymentRequestController {

    private final GRNPaymentRequestService paymentRequestService;

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserProfile) {
            return ((UserProfile) auth.getPrincipal()).getUserId();
        }
        return 1L; // Default fallback
    }

    private Long getCurrentUserBranchId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserProfile) {
            UserProfile user = (UserProfile) auth.getPrincipal();
            return user.getBranch() != null ? user.getBranch().getBranchId() : 1L;
        }
        return 1L;
    }

    /**
     * Get payment requests for current branch (for POS display)
     */
    @GetMapping("/branch")
    public ResponseEntity<ApiResponse<List<GRNPaymentRequestDTO>>> getPaymentRequestsByBranch(
            @RequestParam(required = false) Long branchId) {
        Long targetBranchId = branchId != null ? branchId : getCurrentUserBranchId();
        List<GRNPaymentRequestDTO> requests = paymentRequestService.getPaymentRequestsByBranch(targetBranchId);
        return ResponseEntity.ok(ApiResponse.success("Payment requests fetched", requests));
    }

    /**
     * Get pending payment requests count for POS notification badge
     */
    @GetMapping("/branch/pending-count")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getPendingCount(
            @RequestParam(required = false) Long branchId) {
        Long targetBranchId = branchId != null ? branchId : getCurrentUserBranchId();
        Long count = paymentRequestService.getPendingCountByBranch(targetBranchId);
        return ResponseEntity.ok(ApiResponse.success("Count fetched", Map.of("count", count)));
    }

    /**
     * Get payment requests by status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<GRNPaymentRequestDTO>>> getPaymentRequestsByStatus(
            @PathVariable String status) {
        List<GRNPaymentRequestDTO> requests = paymentRequestService.getPaymentRequestsByStatus(status);
        return ResponseEntity.ok(ApiResponse.success("Payment requests fetched", requests));
    }

    /**
     * Get payment requests for manager (transferred ones)
     */
    @GetMapping("/manager")
    public ResponseEntity<ApiResponse<List<GRNPaymentRequestDTO>>> getManagerPaymentRequests() {
        List<GRNPaymentRequestDTO> requests = paymentRequestService.getManagerPaymentRequests();
        return ResponseEntity.ok(ApiResponse.success("Manager payment requests fetched", requests));
    }

    /**
     * Get manager pending count for notification
     */
    @GetMapping("/manager/pending-count")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getManagerPendingCount() {
        Long count = paymentRequestService.getManagerPendingCount();
        return ResponseEntity.ok(ApiResponse.success("Count fetched", Map.of("count", count)));
    }

    /**
     * Get single payment request by ID
     */
    @GetMapping("/{requestId}")
    public ResponseEntity<ApiResponse<GRNPaymentRequestDTO>> getPaymentRequestById(
            @PathVariable Long requestId) {
        GRNPaymentRequestDTO request = paymentRequestService.getPaymentRequestById(requestId);
        return ResponseEntity.ok(ApiResponse.success("Payment request fetched", request));
    }

    /**
     * Transfer payment request to manager (requires supervisor approval)
     */
    @PostMapping("/{requestId}/transfer-to-manager")
    public ResponseEntity<ApiResponse<GRNPaymentRequestDTO>> transferToManager(
            @PathVariable Long requestId,
            @RequestBody TransferToManagerRequest request) {
        try {
            Long userId = getCurrentUserId();
            GRNPaymentRequestDTO result = paymentRequestService.transferToManager(requestId, request, userId);
            return ResponseEntity.ok(ApiResponse.success("Request transferred to manager", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Manager processes the payment
     */
    @PostMapping("/{requestId}/process-payment")
    public ResponseEntity<ApiResponse<GRNPaymentRequestDTO>> processPayment(
            @PathVariable Long requestId,
            @RequestBody ProcessPaymentRequest request) {
        try {
            Long userId = getCurrentUserId();
            GRNPaymentRequestDTO result = paymentRequestService.processPayment(requestId, request, userId);
            return ResponseEntity.ok(ApiResponse.success("Payment processed successfully", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * Reject a payment request
     */
    @PostMapping("/{requestId}/reject")
    public ResponseEntity<ApiResponse<GRNPaymentRequestDTO>> rejectRequest(
            @PathVariable Long requestId,
            @RequestBody Map<String, String> body) {
        try {
            Long userId = getCurrentUserId();
            String reason = body.getOrDefault("reason", "No reason provided");
            GRNPaymentRequestDTO result = paymentRequestService.rejectRequest(requestId, reason, userId);
            return ResponseEntity.ok(ApiResponse.success("Request rejected", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
