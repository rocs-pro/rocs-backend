package com.nsbm.rocs.manager.controller;

import com.nsbm.rocs.common.response.ApiResponse;
import com.nsbm.rocs.entity.audit.BranchActivity;
import com.nsbm.rocs.service.audit.ActivityLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/manager/activity")
@CrossOrigin
public class BranchActivityController {

    @Autowired
    private ActivityLogService activityLogService;

    @GetMapping("/recent")
    public ResponseEntity<ApiResponse<List<BranchActivity>>> getRecentActivity(@RequestParam(defaultValue = "1") Long branchId) {
        try {
            List<BranchActivity> activities = activityLogService.getRecentActivities(branchId);
            return ResponseEntity.ok(ApiResponse.success("Recent activity fetched", activities));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/filter")
    public ResponseEntity<ApiResponse<List<BranchActivity>>> getActivityByDate(
            @RequestParam(defaultValue = "1") Long branchId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        
        try {
            List<BranchActivity> activities = activityLogService.getActivitiesByDateRange(branchId, start, end);
            return ResponseEntity.ok(ApiResponse.success("Filtered activities fetched", activities));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
