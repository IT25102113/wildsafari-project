package com.safari.module.allocation_mgmt;

import com.safari.common.NotificationService;
import com.safari.common.UserSession;
import com.safari.module.user_mgmt.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/guide-portal")
public class GuidePortalController {

    private final AllocationService allocationService;
    private final NotificationService notificationService;

    public GuidePortalController(AllocationService allocationService,
                                 NotificationService notificationService) {
        this.allocationService = allocationService;
        this.notificationService = notificationService;
    }

    // ─── Dashboard ───────────────────────────────────────────────────
    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session, RedirectAttributes ra) {
        User user = UserSession.getLoggedInUser(session);
        if (user == null || !"GUIDE".equalsIgnoreCase(user.getRole())) {
            ra.addFlashAttribute("errorMessage", "Access restricted to registered guides.");
            return "redirect:/login";
        }

        Guide guide = allocationService.findGuideByEmail(user.getEmail()).orElse(null);
        if (guide == null) {
            ra.addFlashAttribute("errorMessage", "No guide profile linked to this account. Contact your operations manager.");
            return "redirect:/login";
        }

        List<TripAllocation> assignments = allocationService.getAllocationsByGuideEmail(user.getEmail());
        List<GuideAvailability> unavailableDates = allocationService.getGuideAvailabilityByEmail(user.getEmail());
        long unreadCount = notificationService.countUnread(user.getEmail());
        var notifications = notificationService.getAll(user.getEmail());

        model.addAttribute("guide", guide);
        model.addAttribute("assignments", assignments);
        model.addAttribute("unavailableDates", unavailableDates);
        model.addAttribute("notifications", notifications);
        model.addAttribute("unreadCount", unreadCount);
        model.addAttribute("currentUser", user);
        model.addAttribute("currentRole", "GUIDE");
        model.addAttribute("today", LocalDate.now());
        return "guide/dashboard";
    }

    // ─── Mark Unavailable Date ────────────────────────────────────────
    @PostMapping("/unavailable/add")
    public String addUnavailable(@RequestParam("date") LocalDate date,
                                 @RequestParam(value = "reason", defaultValue = "PERSONAL_LEAVE") String reason,
                                 HttpSession session, RedirectAttributes ra) {
        User user = UserSession.getLoggedInUser(session);
        if (user == null || !"GUIDE".equalsIgnoreCase(user.getRole())) return "redirect:/login";

        Guide guide = allocationService.findGuideByEmail(user.getEmail()).orElse(null);
        if (guide == null) { ra.addFlashAttribute("errorMessage", "Guide profile not found."); return "redirect:/guide-portal/dashboard"; }

        if (date.isBefore(LocalDate.now())) {
            ra.addFlashAttribute("errorMessage", "Cannot mark past dates as unavailable.");
            return "redirect:/guide-portal/dashboard";
        }

        try {
            allocationService.markUnavailable(guide.getId(), date, reason);
            ra.addFlashAttribute("successMessage", "Marked " + date + " as unavailable.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/guide-portal/dashboard";
    }

    // ─── Remove Unavailable Date ──────────────────────────────────────
    @PostMapping("/unavailable/remove/{id}")
    public String removeUnavailable(@PathVariable("id") Long id,
                                    HttpSession session, RedirectAttributes ra) {
        User user = UserSession.getLoggedInUser(session);
        if (user == null || !"GUIDE".equalsIgnoreCase(user.getRole())) return "redirect:/login";

        try {
            allocationService.removeUnavailableDate(id);
            ra.addFlashAttribute("successMessage", "Availability restored.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/guide-portal/dashboard";
    }

    // ─── Mark Notification as Read ────────────────────────────────────
    @PostMapping("/notifications/read/{id}")
    public String markRead(@PathVariable("id") Long id,
                           HttpSession session) {
        notificationService.markRead(id);
        return "redirect:/guide-portal/dashboard";
    }

    // ─── Mark All Notifications Read ──────────────────────────────────
    @PostMapping("/notifications/read-all")
    public String markAllRead(HttpSession session) {
        User user = UserSession.getLoggedInUser(session);
        if (user != null) notificationService.markAllRead(user.getEmail());
        return "redirect:/guide-portal/dashboard";
    }

    // ─── Guide Self Profile & Password Update ─────────────────────────
    @PostMapping("/profile/update")
    public String updateSelfProfile(@RequestParam("contactNumber") String contactNumber,
                                    @RequestParam("languages") String languages,
                                    @RequestParam(value = "newPassword", required = false) String newPassword,
                                    @RequestParam(value = "confirmPassword", required = false) String confirmPassword,
                                    HttpSession session,
                                    RedirectAttributes ra) {
        User user = UserSession.getLoggedInUser(session);
        if (user == null || !"GUIDE".equalsIgnoreCase(user.getRole())) {
            ra.addFlashAttribute("errorMessage", "Access restricted to registered guides.");
            return "redirect:/login";
        }

        if (contactNumber == null || !contactNumber.matches("^0[0-9]{9}$|^[0-9]{10}$")) {
            ra.addFlashAttribute("errorMessage", "Validation failed: Contact mobile number must be exactly 10 digits.");
            return "redirect:/guide-portal/dashboard";
        }

        if (languages == null || languages.trim().isBlank()) {
            ra.addFlashAttribute("errorMessage", "Languages spoken cannot be empty.");
            return "redirect:/guide-portal/dashboard";
        }

        if (newPassword != null && !newPassword.isBlank()) {
            if (newPassword.trim().length() < 6) {
                ra.addFlashAttribute("errorMessage", "New password must be at least 6 characters long.");
                return "redirect:/guide-portal/dashboard";
            }
            if (!newPassword.equals(confirmPassword)) {
                ra.addFlashAttribute("errorMessage", "New password confirmation does not match.");
                return "redirect:/guide-portal/dashboard";
            }
        }

        try {
            allocationService.updateGuideSelfProfile(user.getEmail(), contactNumber.trim(), languages.trim(), newPassword);

            // Update user in current session
            user.setPhone(contactNumber.trim());
            if (newPassword != null && !newPassword.isBlank()) {
                user.setPassword(newPassword.trim());
            }
            UserSession.setLoggedInUser(session, user);

            ra.addFlashAttribute("successMessage", "Your guide profile and security credentials have been updated successfully!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/guide-portal/dashboard";
    }
}
