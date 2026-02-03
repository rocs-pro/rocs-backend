package com.nsbm.rocs.manager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PendingGrnDTO {
    private String id;
    private String supplier;
    private Integer items;
    private String eta;
}

