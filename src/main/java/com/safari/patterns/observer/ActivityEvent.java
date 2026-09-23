package com.safari.patterns.observer;

import org.springframework.context.ApplicationEvent;

/**
 * Observer Pattern / Spring Event: Decoupled domain event for system audit logging.
 */
public class ActivityEvent extends ApplicationEvent {

    private final String userEmail;
    private final String role;
    private final String moduleName;
    private final String action;
    private final String details;

    public ActivityEvent(Object source, String userEmail, String role, String moduleName, String action, String details) {
        super(source);
        this.userEmail = userEmail;
        this.role = role;
        this.moduleName = moduleName;
        this.action = action;
        this.details = details;
    }

    public String getUserEmail() { return userEmail; }
    public String getRole() { return role; }
    public String getModuleName() { return moduleName; }
    public String getAction() { return action; }
    public String getDetails() { return details; }
}
