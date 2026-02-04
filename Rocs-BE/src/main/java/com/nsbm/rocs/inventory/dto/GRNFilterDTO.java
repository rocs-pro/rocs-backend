package com.nsbm.rocs.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GRNFilterDTO {

    private Long branchId;
    private Long supplierId;
    private String status;
    private String paymentStatus;
    private LocalDate startDate;
    private LocalDate endDate;
    private String grnNo;
    private String invoiceNo;
}
