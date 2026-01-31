package com.nsbm.rocs.dashboard.manager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO for creating Journal Entry
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JournalEntryCreateDTO {

    @NotNull(message = "Date is required")
    private LocalDate date;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Lines are required")
    private List<LineDTO> lines;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LineDTO {
        private String account;
        private BigDecimal dr;
        private BigDecimal cr;
    }
}

