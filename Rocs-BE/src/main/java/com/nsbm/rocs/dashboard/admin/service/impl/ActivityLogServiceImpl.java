package com.nsbm.rocs.dashboard.admin.service.impl;

import com.nsbm.rocs.dashboard.admin.service.ActivityLogService;
import com.nsbm.rocs.entity.audit.UserActivityLog;
import com.nsbm.rocs.repository.audit.UserActivityLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ActivityLogServiceImpl implements ActivityLogService {

    private final UserActivityLogRepository logRepository;

    @Autowired
    public ActivityLogServiceImpl(UserActivityLogRepository logRepository) {
        this.logRepository = logRepository;
    }

    @Override
    public List<UserActivityLog> getAllLogs() {
        return logRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    @Override
    public List<UserActivityLog> getLogsByFilter(String type, LocalDateTime startDate, LocalDateTime endDate) {
        if (type != null && !type.isEmpty() && startDate != null && endDate != null) {
            return logRepository.findByActivityTypeAndCreatedAtBetween(type, startDate, endDate);
        } else if (type != null && !type.isEmpty()) {
            return logRepository.findByActivityType(type);
        } else if (startDate != null && endDate != null) {
            return logRepository.findByCreatedAtBetween(startDate, endDate);
        } else {
            return getAllLogs();
        }
    }

    @Override
    public List<UserActivityLog> searchLogs(String query, String type, LocalDateTime startDate, LocalDateTime endDate) {
        List<UserActivityLog> logs = getLogsByFilter(type, startDate, endDate);
        if (query == null || query.isEmpty()) {
            return logs;
        }
        String q = query.toLowerCase();
        return logs.stream()
                .filter(log -> (log.getDescription() != null && log.getDescription().toLowerCase().contains(q)) ||
                        (log.getActivityType() != null && log.getActivityType().toLowerCase().contains(q)) ||
                        (log.getUserId() != null && log.getUserId().toString().contains(q)))
                .toList();
    }
}
