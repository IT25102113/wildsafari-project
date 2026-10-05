package com.safari.module.conservation_mgmt;

import com.safari.common.NotificationService;
import com.safari.common.UserSession;
import com.safari.module.booking_mgmt.BookingService;
import com.safari.module.user_mgmt.User;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/conservation")
public class ConservationController {

    private final ConservationService conservationService;
    private final BookingService bookingService;
    private final NotificationService notificationService;

    public ConservationController(ConservationService conservationService,
                                  BookingService bookingService,
                                  NotificationService notificationService) {
        this.conservationService = conservationService;
        this.bookingService = bookingService;
        this.notificationService = notificationService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        User user = UserSession.getLoggedInUser(session);
        if (user == null) {
            return "redirect:/login?redirect=/conservation/dashboard";
        }
        if (!"CONSERVATION_OFFICER".equalsIgnoreCase(user.getRole()) && !"ADMIN".equalsIgnoreCase(user.getRole())) {
            return "redirect:/";
        }

        model.addAttribute("currentUser", user);
        model.addAttribute("currentRole", user.getRole());
        model.addAttribute("permits", conservationService.getAllPermits());
        model.addAttribute("sightings", conservationService.getAllSightings());
        model.addAttribute("incidents", conservationService.getAllIncidents());
        model.addAttribute("summary", conservationService.getComplianceSummary());
        model.addAttribute("bookings", bookingService.getAllBookings());

        // Notifications & Advisories
        model.addAttribute("notifications", notificationService.getAll(user.getEmail()));
        model.addAttribute("unreadCount", notificationService.countUnread(user.getEmail()));

        model.addAttribute("newPermit", new ParkPermit());
        model.addAttribute("newSighting", new WildlifeSighting());
        model.addAttribute("newIncident", new IncidentReport());

        return "conservation/dashboard";
    }

    @PostMapping("/permits/issue")
    public String issuePermit(@Valid @ModelAttribute("newPermit") ParkPermit permit,
                              BindingResult bindingResult,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        User user = UserSession.getLoggedInUser(session);
        String actorEmail = (user != null) ? user.getEmail() : "ranger@safari.lk";

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please provide valid permit information.");
            return "redirect:/conservation/dashboard";
        }

        try {
            if (permit.getIssuingOfficer() == null || permit.getIssuingOfficer().isBlank()) {
                permit.setIssuingOfficer((user != null) ? user.getFullName() : "DWC Ranger");
            }
            conservationService.issuePermit(permit, actorEmail);
            redirectAttributes.addFlashAttribute("successMessage", "Park entry permit generated and recorded successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/conservation/dashboard";
    }

    @PostMapping("/sightings/record")
    public String recordSighting(@Valid @ModelAttribute("newSighting") WildlifeSighting sighting,
                                 BindingResult bindingResult,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        User user = UserSession.getLoggedInUser(session);
        String actorEmail = (user != null) ? user.getEmail() : "ranger@safari.lk";

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please enter complete wildlife sighting details.");
            return "redirect:/conservation/dashboard";
        }

        try {
            conservationService.recordSighting(sighting, actorEmail);
            redirectAttributes.addFlashAttribute("successMessage", "Wildlife observation logged into scientific database.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/conservation/dashboard";
    }

    @PostMapping("/incidents/report")
    public String reportIncident(@Valid @ModelAttribute("newIncident") IncidentReport incident,
                                 BindingResult bindingResult,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        User user = UserSession.getLoggedInUser(session);
        String actorEmail = (user != null) ? user.getEmail() : "ranger@safari.lk";

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "All incident fields are required.");
            return "redirect:/conservation/dashboard";
        }

        try {
            conservationService.reportIncident(incident, actorEmail);
            redirectAttributes.addFlashAttribute("successMessage", "Park safety incident report filed for investigation.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/conservation/dashboard";
    }

    @PostMapping("/permits/update-status/{id}")
    public String updatePermitStatus(@PathVariable("id") Long id,
                                     @RequestParam("status") String status,
                                     HttpSession session,
                                     RedirectAttributes redirectAttributes) {
        User user = UserSession.getLoggedInUser(session);
        String actorEmail = (user != null) ? user.getEmail() : "ranger@safari.lk";

        try {
            conservationService.updatePermitStatus(id, status, actorEmail);
            redirectAttributes.addFlashAttribute("successMessage", "Permit status updated to " + status);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/conservation/dashboard";
    }

    @PostMapping("/permits/delete/{id}")
    public String deletePermit(@PathVariable("id") Long id,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        User user = UserSession.getLoggedInUser(session);
        String actorEmail = (user != null) ? user.getEmail() : "ranger@safari.lk";

        try {
            conservationService.deletePermit(id, actorEmail);
            redirectAttributes.addFlashAttribute("successMessage", "Park permit record successfully deleted.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/conservation/dashboard";
    }

    @PostMapping("/sightings/delete/{id}")
    public String deleteSighting(@PathVariable("id") Long id,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        User user = UserSession.getLoggedInUser(session);
        String actorEmail = (user != null) ? user.getEmail() : "ranger@safari.lk";

        try {
            conservationService.deleteSighting(id, actorEmail);
            redirectAttributes.addFlashAttribute("successMessage", "Sighting record removed.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/conservation/dashboard";
    }

    @PostMapping("/incidents/update-status/{id}")
    public String updateIncidentStatus(@PathVariable("id") Long id,
                                       @RequestParam("status") String status,
                                       @RequestParam(value = "actionTaken", required = false) String actionTaken,
                                       HttpSession session,
                                       RedirectAttributes redirectAttributes) {
        User user = UserSession.getLoggedInUser(session);
        String actorEmail = (user != null) ? user.getEmail() : "ranger@safari.lk";

        try {
            conservationService.updateIncidentStatus(id, status, actionTaken, actorEmail);
            redirectAttributes.addFlashAttribute("successMessage", "Incident investigation status updated.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/conservation/dashboard";
    }

    @PostMapping("/incidents/delete/{id}")
    public String deleteIncident(@PathVariable("id") Long id,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        User user = UserSession.getLoggedInUser(session);
        String actorEmail = (user != null) ? user.getEmail() : "ranger@safari.lk";

        try {
            conservationService.deleteIncident(id, actorEmail);
            redirectAttributes.addFlashAttribute("successMessage", "Incident report record deleted.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/conservation/dashboard";
    }

    // ─── Broadcast DWC Park Alert ──────────────────────────────────────────
    @GetMapping("/broadcast")
    public String broadcastGet() {
        return "redirect:/conservation/dashboard";
    }

    @PostMapping("/broadcast")
    public String broadcastAlert(@RequestParam("targetAudience") String targetAudience,
                                 @RequestParam("parkName") String parkName,
                                 @RequestParam("alertLevel") String alertLevel,
                                 @RequestParam("title") String title,
                                 @RequestParam("message") String message,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        User user = UserSession.getLoggedInUser(session);
        if (user == null) return "redirect:/login";
        if (!"CONSERVATION_OFFICER".equalsIgnoreCase(user.getRole()) && !"ADMIN".equalsIgnoreCase(user.getRole())) {
            return "redirect:/";
        }

        if (title == null || title.isBlank() || message == null || message.isBlank()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Advisory headline and directive details cannot be empty.");
            return "redirect:/conservation/dashboard";
        }

        try {
            int count = conservationService.broadcastAlert(targetAudience, parkName, alertLevel, title.trim(), message.trim(), user.getEmail());
            redirectAttributes.addFlashAttribute("successMessage", "📢 Official DWC Wildlife Advisory successfully broadcasted to " + count + " field & operations personnel!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to broadcast advisory: " + e.getMessage());
        }

        return "redirect:/conservation/dashboard";
    }

    // ─── Notification Status Actions ───────────────────────────────────────
    @PostMapping("/notifications/read/{id}")
    public String markRead(@PathVariable Long id, HttpSession session) {
        User user = UserSession.getLoggedInUser(session);
        if (user != null) {
            notificationService.markRead(id);
        }
        return "redirect:/conservation/dashboard#notificationsSection";
    }

    @PostMapping("/notifications/read-all")
    public String markAllRead(HttpSession session) {
        User user = UserSession.getLoggedInUser(session);
        if (user != null) {
            notificationService.markAllRead(user.getEmail());
        }
        return "redirect:/conservation/dashboard#notificationsSection";
    }
}
