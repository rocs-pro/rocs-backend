package com.nsbm.rocs.dashboard.manager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for Journal Entry response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JournalEntryDTO {
    private String id;
    private String date;
    private String description;
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

