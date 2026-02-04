package com.nsbm.rocs.inventory.controller;

import com.nsbm.rocs.common.response.ApiResponse;
import com.nsbm.rocs.inventory.dto.*;
import com.nsbm.rocs.inventory.exception.GRNException;
import com.nsbm.rocs.inventory.service.GRNService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/api/inventory/grn")
@Validated
public class GRNController {

    @Autowired
    private GRNService grnService;

    /**
     * Create a new GRN
     * POST /api/inventory/grn
     */
    @PostMapping
    public ResponseEntity<ApiResponse<GRNResponseDTO>> createGRN(
            @Valid @RequestBody GRNCreateRequestDTO request,
            @RequestHeader("User-ID") Long currentUserId) {

        try {
            GRNResponseDTO result = grnService.createGRN(request, currentUserId);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("GRN created successfully", result));
        } catch (GRNException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to create GRN: " + e.getMessage()));
        }
    }

    /**
     * Get GRN by ID
     * GET /api/inventory/grn/{grnId}
     */
    @GetMapping("/{grnId}")
    public ResponseEntity<ApiResponse<GRNResponseDTO>> getGRNById(
            @PathVariable @NotNull Long grnId) {

        try {
            GRNResponseDTO result = grnService.getGRNById(grnId);
            return ResponseEntity.ok(ApiResponse.success("GRN retrieved successfully", result));
        } catch (GRNException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve GRN: " + e.getMessage()));
        }
    }

    /**
     * Get all GRNs for a branch
     * GET /api/inventory/grn/branch/{branchId}
     */
    @GetMapping("/branch/{branchId}")
    public ResponseEntity<ApiResponse<List<GRNResponseDTO>>> getGRNsByBranch(
            @PathVariable @NotNull Long branchId) {

        try {
            List<GRNResponseDTO> result = grnService.getGRNsByBranch(branchId);
            return ResponseEntity.ok(ApiResponse.success("GRNs retrieved successfully", result));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve GRNs: " + e.getMessage()));
        }
    }

    /**
     * Search GRNs with filters
     * POST /api/inventory/grn/search
     */
    @PostMapping("/search")
    public ResponseEntity<ApiResponse<List<GRNResponseDTO>>> searchGRNs(
            @RequestBody GRNFilterDTO filter) {

        try {
            List<GRNResponseDTO> result = grnService.searchGRNs(filter);
            return ResponseEntity.ok(ApiResponse.success("GRNs retrieved successfully", result));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to search GRNs: " + e.getMessage()));
        }
    }

    /**
     * Update GRN (only if pending)
     * PUT /api/inventory/grn/{grnId}
     */
    @PutMapping("/{grnId}")
    public ResponseEntity<ApiResponse<GRNResponseDTO>> updateGRN(
            @PathVariable @NotNull Long grnId,
            @Valid @RequestBody GRNUpdateRequestDTO request) {

        try {
            GRNResponseDTO result = grnService.updateGRN(grnId, request);
            return ResponseEntity.ok(ApiResponse.success("GRN updated successfully", result));
        } catch (GRNException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to update GRN: " + e.getMessage()));
        }
    }

    /**
     * Approve GRN
     * PUT /api/inventory/grn/{grnId}/approve
     */
    @PutMapping("/{grnId}/approve")
    public ResponseEntity<ApiResponse<GRNResponseDTO>> approveGRN(
            @PathVariable @NotNull Long grnId,
            @RequestHeader("User-ID") Long approvedBy) {

        try {
            GRNResponseDTO result = grnService.approveGRN(grnId, approvedBy);
            return ResponseEntity.ok(ApiResponse.success("GRN approved successfully", result));
        } catch (GRNException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to approve GRN: " + e.getMessage()));
        }
    }

    /**
     * Update GRN payment status
     * PUT /api/inventory/grn/{grnId}/payment-status
     */
    @PutMapping("/{grnId}/payment-status")
    public ResponseEntity<ApiResponse<GRNResponseDTO>> updatePaymentStatus(
            @PathVariable @NotNull Long grnId,
            @RequestParam String paymentStatus) {

        try {
            GRNResponseDTO result = grnService.updatePaymentStatus(grnId, paymentStatus);
            return ResponseEntity.ok(ApiResponse.success("Payment status updated successfully", result));
        } catch (GRNException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to update payment status: " + e.getMessage()));
        }
    }

    /**
     * Delete GRN (only if pending)
     * DELETE /api/inventory/grn/{grnId}
     */
    @DeleteMapping("/{grnId}")
    public ResponseEntity<ApiResponse<Void>> deleteGRN(
            @PathVariable @NotNull Long grnId) {

        try {
            grnService.deleteGRN(grnId);
            return ResponseEntity.ok(ApiResponse.success("GRN deleted successfully", null));
        } catch (GRNException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to delete GRN: " + e.getMessage()));
        }
    }

    /**
     * Get GRN statistics for a branch
     * GET /api/inventory/grn/branch/{branchId}/stats
     */
    @GetMapping("/branch/{branchId}/stats")
    public ResponseEntity<ApiResponse<GRNStatsDTO>> getGRNStats(
            @PathVariable @NotNull Long branchId,
            @RequestParam(required = false) String period) {

        try {
            GRNStatsDTO result = grnService.getGRNStats(branchId, period);
            return ResponseEntity.ok(ApiResponse.success("GRN stats retrieved successfully", result));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve GRN stats: " + e.getMessage()));
        }
    }

    /**
     * Reject GRN
     * PUT /api/inventory/grn/{grnId}/reject
     */
    @PutMapping("/{grnId}/reject")
    public ResponseEntity<ApiResponse<GRNResponseDTO>> rejectGRN(
            @PathVariable @NotNull Long grnId,
            @RequestHeader("User-ID") Long rejectedBy,
            @RequestParam(required = false) String reason) {

        try {
            GRNResponseDTO result = grnService.rejectGRN(grnId, rejectedBy, reason);
            return ResponseEntity.ok(ApiResponse.success("GRN rejected successfully", result));
        } catch (GRNException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to reject GRN: " + e.getMessage()));
        }
    }

    /**
     * Get GRN items by product
     * GET /api/inventory/grn/product/{productId}/items
     */
    @GetMapping("/product/{productId}/items")
    public ResponseEntity<ApiResponse<List<GRNItemDTO>>> getGRNItemsByProduct(
            @PathVariable @NotNull Long productId,
            @RequestParam(required = false) Long branchId) {

        try {
            List<GRNItemDTO> result = grnService.getGRNItemsByProduct(productId, branchId);
            return ResponseEntity.ok(ApiResponse.success("GRN items retrieved successfully", result));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve GRN items: " + e.getMessage()));
        }
    }

    /**
     * Check if GRN number exists
     * GET /api/inventory/grn/check-number/{grnNo}
     */
    @GetMapping("/check-number/{grnNo}")
    public ResponseEntity<ApiResponse<Boolean>> checkGRNNumber(
            @PathVariable @NotNull String grnNo) {

        try {
            boolean exists = grnService.isGRNNumberExists(grnNo);
            return ResponseEntity.ok(ApiResponse.success("GRN number check completed", exists));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to check GRN number: " + e.getMessage()));
        }
    }

    /**
     * Get GRNs by supplier
     * GET /api/inventory/grn/supplier/{supplierId}
     */
    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<ApiResponse<List<GRNResponseDTO>>> getGRNsBySupplier(
            @PathVariable @NotNull Long supplierId) {

        try {
            GRNFilterDTO filter = new GRNFilterDTO();
            filter.setSupplierId(supplierId);
            List<GRNResponseDTO> result = grnService.searchGRNs(filter);
            return ResponseEntity.ok(ApiResponse.success("GRNs retrieved successfully", result));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve GRNs: " + e.getMessage()));
        }
    }

    /**
     * Get pending GRNs for approval
     * GET /api/inventory/grn/pending
     */
    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<GRNResponseDTO>>> getPendingGRNs(
            @RequestParam(required = false) Long branchId) {

        try {
            GRNFilterDTO filter = new GRNFilterDTO();
            filter.setStatus("PENDING");
            if (branchId != null) {
                filter.setBranchId(branchId);
            }
            List<GRNResponseDTO> result = grnService.searchGRNs(filter);
            return ResponseEntity.ok(ApiResponse.success("Pending GRNs retrieved successfully", result));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve pending GRNs: " + e.getMessage()));
        }
    }
}


