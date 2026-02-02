package com.nsbm.rocs.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSerialDTO {
    private Long serialId;
    private Long productId;
    private String productName;
    private String productSku;
    private Long branchId;
    private String branchName;
    private String serialNo;
    private String barcode;
    private Long batchId;
    private String batchCode;
    private String status;
    private Long grnId;
    private Long saleId;
    private LocalDateTime createdAt;
    private LocalDateTime soldAt;
}

