package com.safari.module.allocation_mgmt;

import com.safari.common.UserSession;
import com.safari.module.booking_mgmt.Booking;
import com.safari.module.booking_mgmt.BookingService;
import com.safari.module.user_mgmt.User;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/allocation")
public class AllocationController {

    private final AllocationService allocationService;
    private final BookingService bookingService;

    public AllocationController(AllocationService allocationService, BookingService bookingService) {
        this.allocationService = allocationService;
        this.bookingService = bookingService;
    }

    @GetMapping("/dashboard")
    public String allocationDashboard(Model model, HttpSession session) {
        User user = UserSession.getLoggedInUser(session);
        if (user == null) {
            return "redirect:/login?redirect=/allocation/dashboard";
        }
        if (!"OPERATIONS_MANAGER".equalsIgnoreCase(user.getRole()) && !"ADMIN".equalsIgnoreCase(user.getRole())) {
            return "redirect:/";
        }

        List<Booking> allBookings = bookingService.getAllBookings();
        List<TripAllocation> allocations = allocationService.getAllAllocations();
        List<Guide> guides = allocationService.getActiveGuides();
        List<Vehicle> vehicles = allocationService.getAvailableVehicles();

        model.addAttribute("currentUser", user);
        model.addAttribute("currentRole", user.getRole());
        model.addAttribute("bookings", allBookings);
        model.addAttribute("allocations", allocations);
        model.addAttribute("guides", guides);
        model.addAttribute("vehicles", vehicles);

        return "allocation/dashboard";
    }

    @PostMapping("/assign")
    public String assignCrew(@RequestParam("bookingId") Long bookingId,
                             @RequestParam("guideId") Long guideId,
                             @RequestParam("vehicleId") Long vehicleId,
                             @RequestParam(value = "dispatchNotes", required = false) String dispatchNotes,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        User user = UserSession.getLoggedInUser(session);
        String actorEmail = (user != null) ? user.getEmail() : "ops@safari.lk";

        try {
            allocationService.assignCrew(bookingId, guideId, vehicleId, dispatchNotes, actorEmail);
            redirectAttributes.addFlashAttribute("successMessage", "Crew successfully assigned to booking without any scheduling conflicts!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/allocation/dashboard";
    }

    @PostMapping("/reassign/{id}")
    public String reassignCrew(@PathVariable("id") Long allocationId,
                               @RequestParam("guideId") Long guideId,
                               @RequestParam("vehicleId") Long vehicleId,
                               @RequestParam(value = "notes", required = false) String notes,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        User user = UserSession.getLoggedInUser(session);
        String actorEmail = (user != null) ? user.getEmail() : "ops@safari.lk";

        try {
            allocationService.reassignCrew(allocationId, guideId, vehicleId, notes, actorEmail);
            redirectAttributes.addFlashAttribute("successMessage", "Crew emergency reassignment saved successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/allocation/dashboard";
    }

    @PostMapping("/remove/{id}")
    public String removeAllocation(@PathVariable("id") Long allocationId,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        User user = UserSession.getLoggedInUser(session);
        String actorEmail = (user != null) ? user.getEmail() : "ops@safari.lk";
        allocationService.removeAllocation(allocationId, actorEmail);
        redirectAttributes.addFlashAttribute("successMessage", "Trip allocation removed.");
        return "redirect:/allocation/dashboard";
    }

    // Guide Management
    @GetMapping("/guides")
    public String manageGuides(Model model, HttpSession session) {
        User user = UserSession.getLoggedInUser(session);
        if (user == null) {
            return "redirect:/login?redirect=/allocation/guides";
        }
        if (!"OPERATIONS_MANAGER".equalsIgnoreCase(user.getRole()) && !"ADMIN".equalsIgnoreCase(user.getRole())) {
            return "redirect:/";
        }

        model.addAttribute("currentUser", user);
        model.addAttribute("currentRole", user.getRole());
        model.addAttribute("guides", allocationService.getAllGuides());
        model.addAttribute("newGuide", new Guide());
        return "allocation/guides";
    }

    @PostMapping("/guides/save")
    public String saveGuide(@Valid @ModelAttribute("newGuide") Guide guide,
                            BindingResult bindingResult,
                            HttpSession session,
                            RedirectAttributes redirectAttributes,
                            Model model) {
        User user = UserSession.getLoggedInUser(session);
        String actorEmail = (user != null) ? user.getEmail() : "ops@safari.lk";

        if (!guide.getContactNumber().matches("^0[0-9]{9}$|^[0-9]{10}$")) {
            bindingResult.rejectValue("contactNumber", "error.newGuide", "Contact number must be exactly 10 digits");
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Validation failed: Please ensure all fields are correct, including a 10-digit phone number.");
            return "redirect:/allocation/guides";
        }

        try {
            allocationService.saveGuide(guide, actorEmail);
            redirectAttributes.addFlashAttribute("successMessage", "Guide record saved successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/allocation/guides";
    }

    // Vehicle Management
    @GetMapping("/vehicles")
    public String manageVehicles(Model model, HttpSession session) {
        User user = UserSession.getLoggedInUser(session);
        if (user == null) {
            return "redirect:/login?redirect=/allocation/vehicles";
        }
        if (!"OPERATIONS_MANAGER".equalsIgnoreCase(user.getRole()) && !"ADMIN".equalsIgnoreCase(user.getRole())) {
            return "redirect:/";
        }

        model.addAttribute("currentUser", user);
        model.addAttribute("currentRole", user.getRole());
        model.addAttribute("vehicles", allocationService.getAllVehicles());
        model.addAttribute("newVehicle", new Vehicle());
        return "allocation/vehicles";
    }

    @PostMapping("/vehicles/save")
    public String saveVehicle(@Valid @ModelAttribute("newVehicle") Vehicle vehicle,
                              BindingResult bindingResult,
                              HttpSession session,
                              RedirectAttributes redirectAttributes,
                              Model model) {
        User user = UserSession.getLoggedInUser(session);
        String actorEmail = (user != null) ? user.getEmail() : "ops@safari.lk";

        if (bindingResult.hasErrors()) {
            model.addAttribute("currentUser", user);
            model.addAttribute("currentRole", UserSession.getCurrentRole(session));
            model.addAttribute("vehicles", allocationService.getAllVehicles());
            return "allocation/vehicles";
        }

        try {
            allocationService.saveVehicle(vehicle, actorEmail);
            redirectAttributes.addFlashAttribute("successMessage", "Vehicle record saved successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/allocation/vehicles";
    }

    @PostMapping("/vehicles/maintenance/{id}")
    public String toggleMaintenance(@PathVariable("id") Long vehicleId,
                                    @RequestParam("maintenance") boolean maintenance,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes) {
        User user = UserSession.getLoggedInUser(session);
        String actorEmail = (user != null) ? user.getEmail() : "ops@safari.lk";
        allocationService.setVehicleMaintenance(vehicleId, maintenance, actorEmail);
        redirectAttributes.addFlashAttribute("successMessage", "Vehicle maintenance state updated.");
        return "redirect:/allocation/vehicles";
    }
}
