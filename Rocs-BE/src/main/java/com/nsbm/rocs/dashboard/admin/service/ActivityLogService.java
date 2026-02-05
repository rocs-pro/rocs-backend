package com.nsbm.rocs.dashboard.admin.service;

import com.nsbm.rocs.entity.audit.UserActivityLog;
import java.time.LocalDateTime;
import java.util.List;

public interface ActivityLogService {
    List<UserActivityLog> getAllLogs();

    List<UserActivityLog> getLogsByFilter(String type, LocalDateTime startDate, LocalDateTime endDate);

    List<UserActivityLog> searchLogs(String query, String type, LocalDateTime startDate, LocalDateTime endDate);
}
