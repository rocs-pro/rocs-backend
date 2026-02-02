package com.nsbm.rocs.dashboard.manager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for dashboard stat cards
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatCardDTO {
    private String title;
    private String value;
    private String icon;
    private String tone; // primary, secondary, warning, danger
}

