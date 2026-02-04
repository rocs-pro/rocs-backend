package com.nsbm.rocs.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GRNUpdateRequestDTO {

    private LocalDate grnDate;
    private String invoiceNo;
    private LocalDate invoiceDate;
    private List<GRNCreateRequestDTO.GRNItemCreateDTO> items;
}
