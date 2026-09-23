package com.safari.patterns.observer;

import com.safari.common.ActivityLogService;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Observer Pattern Listener: Listens for system activity events and persists them asynchronously.
 */
@Component
public class ActivityEventListener {

    private final ActivityLogService activityLogService;

    public ActivityEventListener(ActivityLogService activityLogService) {
        this.activityLogService = activityLogService;
    }

    @EventListener
    public void onActivityEvent(ActivityEvent event) {
        activityLogService.saveLog(
                event.getUserEmail(),
                event.getRole(),
                event.getModuleName(),
                event.getAction(),
                event.getDetails()
        );
    }
}
