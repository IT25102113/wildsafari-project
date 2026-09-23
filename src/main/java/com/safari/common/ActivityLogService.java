package com.safari.common;

import com.safari.patterns.observer.ActivityEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;
    private final ApplicationEventPublisher eventPublisher;

    public ActivityLogService(ActivityLogRepository activityLogRepository, ApplicationEventPublisher eventPublisher) {
        this.activityLogRepository = activityLogRepository;
        this.eventPublisher = eventPublisher;
    }

    public void publishActivity(String userEmail, String role, String moduleName, String action, String details) {
        eventPublisher.publishEvent(new ActivityEvent(this, userEmail, role, moduleName, action, details));
    }

    @Transactional
    public ActivityLog saveLog(String userEmail, String role, String moduleName, String action, String details) {
        ActivityLog log = new ActivityLog(userEmail, role, moduleName, action, details);
        return activityLogRepository.save(log);
    }

    public List<ActivityLog> getRecentLogs() {
        return activityLogRepository.findTop50ByOrderByTimestampDesc();
    }
}
