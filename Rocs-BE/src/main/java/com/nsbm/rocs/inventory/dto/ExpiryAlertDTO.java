package com.nsbm.rocs.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpiryAlertDTO {

    private Long batchId;
    private String batchCode;
    private Long productId;
    private String productName;
    private String productSku;
    private Long branchId;
    private String branchName;
    private Integer qty;
    private LocalDate expiryDate;
    private Long daysToExpiry;
    private String alertLevel; // CRITICAL, WARNING, INFO
    private String message;
}
