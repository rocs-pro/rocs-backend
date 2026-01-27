package com.nsbm.rocs.pos.controller;

import com.nsbm.rocs.pos.dto.sale.CreateSaleRequest;
import com.nsbm.rocs.pos.dto.sale.SaleResponse;
import com.nsbm.rocs.pos.dto.sale.SaleSummaryDTO;
import com.nsbm.rocs.pos.service.PosService;
import com.nsbm.rocs.common.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping("/orders")
    public ResponseEntity<ApiResponse<SaleResponse>> submitOrder(@RequestBody CreateSaleRequest request) {
        // TODO: Get real IDs from SecurityContext
        Long branchId = 1L;
        Long cashierId = 1001L; // logged in user

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
    }

    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<List<SaleSummaryDTO>>> getBills(@RequestParam(required = false) String status) {
        List<SaleSummaryDTO> response = posService.getSaleSummaries(status);
        return new ResponseEntity<>(
                ApiResponse.success("Orders fetched", response),
                HttpStatus.OK
        );
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<ApiResponse<SaleResponse>> getBillById(@PathVariable Long id) {
        SaleResponse response = posService.getSaleById(id);
        return new ResponseEntity<>(
                ApiResponse.success("Order fetched", response),
                HttpStatus.OK
        );
    }
}
