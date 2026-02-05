package com.nsbm.rocs.pos.controller;

import com.nsbm.rocs.entity.main.UserProfile;
import com.nsbm.rocs.pos.dto.sale.CreateSaleRequest;
import com.nsbm.rocs.pos.dto.sale.SaleResponse;
import com.nsbm.rocs.pos.dto.sale.SaleSummaryDTO;
import com.nsbm.rocs.pos.service.PosService;
import com.nsbm.rocs.common.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/pos")
@CrossOrigin
public class PosController {

    private final PosService posService;
    private final com.nsbm.rocs.pos.service.ShiftService shiftService;

    @Autowired
    public PosController(PosService posService, com.nsbm.rocs.pos.service.ShiftService shiftService) {
        this.posService = posService;
        this.shiftService = shiftService;
    }

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserProfile) {
            return ((UserProfile) auth.getPrincipal()).getUserId();
        }
        throw new RuntimeException("User not authenticated");
    }

    private Long getCurrentUserBranchId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserProfile) {
            UserProfile user = (UserProfile) auth.getPrincipal();
            return user.getBranch() != null ? user.getBranch().getBranchId() : 1L;
        }
        return 1L; // Default branch
    }

    @GetMapping({"/sales/last-invoice", "/orders/last-invoice"})
    public ResponseEntity<ApiResponse<Map<String, Object>>> getLastInvoice() {
        try {
            Map<String, Object> invoiceInfo = posService.getLastInvoiceInfo();
            return ResponseEntity.ok(ApiResponse.success("Invoice info fetched", invoiceInfo));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping({"/orders", "/sales"})
    public ResponseEntity<ApiResponse<SaleResponse>> submitOrder(@RequestBody CreateSaleRequest request) {
        try {
            Long cashierId = getCurrentUserId();
            Long branchId = getCurrentUserBranchId();

            Long shiftId;
            try {
                shiftId = shiftService.getActiveShiftId(cashierId);
            } catch (IllegalStateException e) {
                return new ResponseEntity<>(ApiResponse.error(e.getMessage()), HttpStatus.BAD_REQUEST);
            }

            SaleResponse response = posService.createSale(request, branchId, cashierId, shiftId);
            return new ResponseEntity<>(
                    ApiResponse.success("Order created successfully", response),
                    HttpStatus.CREATED
            );
        } catch (Exception e) {
            return new ResponseEntity<>(ApiResponse.error(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping({"/orders", "/sales"})
    public ResponseEntity<ApiResponse<List<SaleSummaryDTO>>> getBills(@RequestParam(required = false) String status) {
        List<SaleSummaryDTO> response = posService.getSaleSummaries(status);
        return new ResponseEntity<>(
                ApiResponse.success("Orders fetched", response),
                HttpStatus.OK
        );
    }

    @GetMapping({"/orders/{id}", "/sales/{id}"})
    public ResponseEntity<ApiResponse<SaleResponse>> getBillById(@PathVariable Long id) {
        SaleResponse response = posService.getSaleById(id);
        return new ResponseEntity<>(
                ApiResponse.success("Order fetched", response),
                HttpStatus.OK
        );
    }

    @PostMapping("/sales/hold")
    public ResponseEntity<ApiResponse<SaleResponse>> holdBill(@RequestBody CreateSaleRequest request) {
        request.setStatus("HELD");
        return submitOrder(request);
    }

    @GetMapping("/sales/held")
    public ResponseEntity<ApiResponse<List<SaleSummaryDTO>>> getHeldBills(@RequestParam(required = false) Long branchId) {
        return ResponseEntity.ok(ApiResponse.success("Fetched held bills", posService.getHeldBills(branchId)));
    }

    @PostMapping("/sales/{id}/recall")
    public ResponseEntity<ApiResponse<SaleResponse>> recallBill(@PathVariable Long id) {
        // Change status to PENDING to effectively extract it from HELD list
        posService.updateSaleStatus(id, "PENDING");
        return getBillById(id);
    }

    @PostMapping("/returns")
    public ResponseEntity<ApiResponse<Map<String, Object>>> processReturn(@RequestBody com.nsbm.rocs.pos.dto.returns.ReturnRequest request) {
        try {
            Long returnId = posService.processReturn(request);
            return ResponseEntity.ok(ApiResponse.success("Return processed successfully", Map.of("returnId", returnId)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}