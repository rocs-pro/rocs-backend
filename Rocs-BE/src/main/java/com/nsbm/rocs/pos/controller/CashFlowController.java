package com.nsbm.rocs.pos.controller;

import com.nsbm.rocs.common.response.ApiResponse;
import com.nsbm.rocs.entity.pos.CashFlow;
import com.nsbm.rocs.pos.dto.CashFlowRequest;
import com.nsbm.rocs.pos.service.ShiftService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pos/cash-flow")
@CrossOrigin
public class CashFlowController {

    private final ShiftService shiftService;

    @Autowired
    public CashFlowController(ShiftService shiftService) {
        this.shiftService = shiftService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CashFlow>> recordCashFlow(@Valid @RequestBody CashFlowRequest request) {
        // TODO: Get real cashier ID from security context
        Long cashierId = 1001L;

        try {
            CashFlow saved = shiftService.recordCashFlow(cashierId, request);
            return new ResponseEntity<>(
                    ApiResponse.success("Cash flow recorded", saved),
                    HttpStatus.CREATED
            );
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(
                    ApiResponse.error(e.getMessage()),
                    HttpStatus.BAD_REQUEST
            );
        }
    }
}
