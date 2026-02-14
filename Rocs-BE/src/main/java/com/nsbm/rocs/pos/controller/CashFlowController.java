package com.nsbm.rocs.pos.controller;

import com.nsbm.rocs.common.response.ApiResponse;
import com.nsbm.rocs.entity.pos.CashFlow;
import com.nsbm.rocs.pos.dto.CashFlowRequest;
import com.nsbm.rocs.pos.repository.CashFlowRepository;
import com.nsbm.rocs.pos.service.ShiftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/pos/cashflow")
@CrossOrigin
public class CashFlowController {

    @Autowired
    private CashFlowRepository cashFlowRepository;

    @Autowired
    private ShiftService shiftService;

    // Removed constructor injection to resolve potential circular dependency/loading issues


    @PostMapping
    public ResponseEntity<ApiResponse<CashFlow>> recordCashFlow(@RequestBody CashFlowRequest request) {
        // TODO: Get real cashierId from SecurityContext
        Long cashierId = 1001L;

        try {
            Long shiftId = shiftService.getActiveShiftId(cashierId);

            CashFlow cashFlow = new CashFlow();
            cashFlow.setShiftId(shiftId);
            cashFlow.setAmount(request.getAmount());
            cashFlow.setType(request.getType()); // PAID_IN or PAID_OUT
            cashFlow.setReason(request.getReason());
            cashFlow.setReferenceNo(request.getReferenceNo());
            cashFlow.setCreatedBy(cashierId);
            cashFlow.setCreatedAt(LocalDateTime.now());

            CashFlow saved = cashFlowRepository.save(cashFlow);

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

    @GetMapping("/shift/{shiftId}")
    public ResponseEntity<ApiResponse<List<CashFlow>>> getCashFlowsByShift(@PathVariable Long shiftId) {
        List<CashFlow> cashFlows = cashFlowRepository.findByShiftId(shiftId);
        return new ResponseEntity<>(
                ApiResponse.success("Cash flows fetched", cashFlows),
                HttpStatus.OK
        );
    }
}

