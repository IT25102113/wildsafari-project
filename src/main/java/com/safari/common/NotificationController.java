package com.safari.common;

import com.safari.module.user_mgmt.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/read/{id}")
    public String markRead(@PathVariable("id") Long id,
                           @RequestHeader(value = "Referer", required = false) String referer) {
        notificationService.markRead(id);
        return "redirect:" + (referer != null && !referer.isBlank() ? referer : "/");
    }

    @PostMapping("/read-all")
    public String markAllRead(HttpSession session,
                              @RequestHeader(value = "Referer", required = false) String referer) {
        User user = UserSession.getLoggedInUser(session);
        if (user != null && user.getEmail() != null) {
            notificationService.markAllRead(user.getEmail());
        }
        return "redirect:" + (referer != null && !referer.isBlank() ? referer : "/");
    }
}
