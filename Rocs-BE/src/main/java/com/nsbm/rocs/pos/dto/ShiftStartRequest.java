package com.nsbm.rocs.pos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShiftStartRequest {
    private Long cashierId;
    private Long branchId;
    private Long terminalId;
    private BigDecimal openingCash;
    private SupervisorAuth supervisor;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SupervisorAuth {
        private String username;
        private String password;
    }
}
