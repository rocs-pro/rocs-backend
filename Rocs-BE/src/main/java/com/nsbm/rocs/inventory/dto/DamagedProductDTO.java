package com.nsbm.rocs.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DamagedProductDTO {

    private Long serialId;
    private String serialNo;
    private Long productId;
    private String productName;
    private String productSku;
    private Long branchId;
    private String branchName;
    private String batchCode;
    private String damageReason;
    private String message;
}
