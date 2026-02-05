package com.nsbm.rocs.manager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityLogDTO {
    private Long activityId;
    private String time;
    private String user;
    private String action;
    private String details;
    private String severity;
}

