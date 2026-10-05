package com.safari.common;

import com.safari.module.user_mgmt.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Collections;
import java.util.List;

@ControllerAdvice
public class GlobalModelAdvice {

    private final NotificationService notificationService;

    public GlobalModelAdvice(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @ModelAttribute
    public void addGlobalAttributes(Model model, HttpSession session) {
        User user = UserSession.getLoggedInUser(session);
        if (user != null && user.getEmail() != null) {
            List<Notification> userNotifs = notificationService.getAll(user.getEmail());
            long unread = notificationService.countUnread(user.getEmail());
            model.addAttribute("navNotifications", userNotifs);
            model.addAttribute("navUnreadCount", unread);
        } else {
            model.addAttribute("navNotifications", Collections.emptyList());
            model.addAttribute("navUnreadCount", 0L);
        }
    }
}
