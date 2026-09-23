package com.safari.module.conservation_mgmt;

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

    public ConservationController(ConservationService conservationService, BookingService bookingService) {
        this.conservationService = conservationService;
        this.bookingService = bookingService;
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
}
