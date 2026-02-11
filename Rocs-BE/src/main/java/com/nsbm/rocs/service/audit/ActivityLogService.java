package com.nsbm.rocs.service.audit;

import com.nsbm.rocs.entity.audit.BranchActivity;
import com.nsbm.rocs.repository.audit.BranchActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ActivityLogService {

    @Autowired
    private BranchActivityRepository activityRepository;

    @Async
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void logActivity(Long branchId, Long terminalId, Long userId, String username, String userRole,
                            String actionType, String entityType, Long entityId, String details, String metadata) {
        
        try {
            BranchActivity activity = BranchActivity.builder()
                    .branchId(branchId != null ? branchId : 1L) 
                    .actionType(actionType) 
                    .userId(userId) 
                    .details(details) 
                    // New fields
                    .terminalId(terminalId)
                    .entityType(entityType)
                    .entityId(entityId) // Long
                    .metadata(metadata)
                    // Defaults
                    .severity("INFO")
                    .status("SUCCESS")
                    .timestamp(LocalDateTime.now()) 
                    .build();

            // Note: username/role are transient or used for display logic upstream, not stored in this refined schema directly 
            // unless added to metadata or if we join with User tables.
            // But we can store them in description/metadata if we really want them snapshot.
            if (username != null || userRole != null) {
                 // Append to metadata if simpler
                 String userMeta = "{\"user\":\"" + username + "\", \"role\":\"" + userRole + "\"}";
                 // Simple append for now
                 if (activity.getMetadata() == null) activity.setMetadata(userMeta);
                 else activity.setMetadata(activity.getMetadata().substring(0, activity.getMetadata().length()-1) + ", \"user_info\":" + userMeta + "}");
            }

            activityRepository.save(activity);
        } catch (Exception e) {
            // Log error silently to avoid disrupting main flow
            System.err.println("Failed to save activity log: " + e.getMessage());
        }
    }

    public List<BranchActivity> getRecentActivities(Long branchId) {
        return activityRepository.findTop20ByBranchIdOrderByTimestampDesc(branchId);
    }

    public List<BranchActivity> getActivitiesByDateRange(Long branchId, LocalDateTime start, LocalDateTime end) {
        return activityRepository.findByBranchIdAndTimestampBetweenOrderByTimestampDesc(branchId, start, end);
    }
}
