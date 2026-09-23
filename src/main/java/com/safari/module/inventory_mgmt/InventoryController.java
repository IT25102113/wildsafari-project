package com.safari.module.inventory_mgmt;

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
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService inventoryService;
    private final BookingService bookingService;

    public InventoryController(InventoryService inventoryService, BookingService bookingService) {
        this.inventoryService = inventoryService;
        this.bookingService = bookingService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        User user = UserSession.getLoggedInUser(session);
        if (user == null) {
            return "redirect:/login?redirect=/inventory/dashboard";
        }
        if (!"LOGISTICS_STAFF".equalsIgnoreCase(user.getRole()) && !"ADMIN".equalsIgnoreCase(user.getRole())) {
            return "redirect:/";
        }

        model.addAttribute("currentUser", user);
        model.addAttribute("currentRole", user.getRole());
        model.addAttribute("equipmentList", inventoryService.getAllEquipment());
        model.addAttribute("lowStockItems", inventoryService.getLowStockEquipment());
        
        // Fetch all bookings and pending requests
        model.addAttribute("bookings", bookingService.getAllBookings());
        model.addAttribute("pendingRequests", inventoryService.getPendingRequests());
        model.addAttribute("activeAllocations", inventoryService.getActiveAllocations());
        model.addAttribute("newEquipment", new Equipment());

        return "inventory/dashboard";
    }

    @PostMapping("/add")
    public String addEquipment(@Valid @ModelAttribute("newEquipment") Equipment equipment,
                               BindingResult bindingResult,
                               HttpSession session,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        User user = UserSession.getLoggedInUser(session);
        String actorEmail = (user != null) ? user.getEmail() : "logistics@safari.lk";

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please provide valid equipment information.");
            return "redirect:/inventory/dashboard";
        }

        try {
            inventoryService.saveEquipment(equipment, actorEmail);
            redirectAttributes.addFlashAttribute("successMessage", "Equipment item added to depot inventory.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/inventory/dashboard";
    }

    @PostMapping("/issue")
    public String issueEquipment(@RequestParam("equipmentId") Long equipmentId,
                                 @RequestParam("bookingId") Long bookingId,
                                 @RequestParam("quantity") int quantity,
                                 @RequestParam(value = "remarks", required = false) String remarks,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        User user = UserSession.getLoggedInUser(session);
        String actorEmail = (user != null) ? user.getEmail() : "logistics@safari.lk";

        try {
            inventoryService.issueEquipmentToBooking(equipmentId, bookingId, quantity, remarks, actorEmail);
            redirectAttributes.addFlashAttribute("successMessage", "Equipment successfully checked out for safari expedition.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/inventory/dashboard";
    }

    @PostMapping("/return/{id}")
    public String returnEquipment(@PathVariable("id") Long allocationId,
                                  @RequestParam(value = "conditionReport", defaultValue = "Good condition") String conditionReport,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        User user = UserSession.getLoggedInUser(session);
        String actorEmail = (user != null) ? user.getEmail() : "logistics@safari.lk";

        try {
            inventoryService.returnEquipment(allocationId, conditionReport, actorEmail);
            redirectAttributes.addFlashAttribute("successMessage", "Equipment successfully returned and inventory restocked.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/inventory/dashboard";
    }

    @PostMapping("/delete/{id}")
    public String decommissionEquipment(@PathVariable("id") Long id,
                                        HttpSession session,
                                        RedirectAttributes redirectAttributes) {
        User user = UserSession.getLoggedInUser(session);
        String actorEmail = (user != null) ? user.getEmail() : "logistics@safari.lk";

        try {
            inventoryService.deleteEquipment(id, actorEmail);
            redirectAttributes.addFlashAttribute("successMessage", "Equipment item decommissioned from inventory.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/inventory/dashboard";
    }

    @PostMapping("/approve/{id}")
    public String approveEquipmentRequest(@PathVariable("id") Long allocationId, HttpSession session, RedirectAttributes redirectAttributes) {
        User user = UserSession.getLoggedInUser(session);
        String actorEmail = (user != null) ? user.getEmail() : "logistics@safari.lk";

        try {
            inventoryService.approveEquipmentRequest(allocationId, actorEmail);
            redirectAttributes.addFlashAttribute("successMessage", "Equipment request approved and issued successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/inventory/dashboard";
    }

    @PostMapping("/adjust-stock/{id}")
    public String adjustStock(@PathVariable("id") Long equipmentId, @RequestParam("change") int change, HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            Equipment eq = inventoryService.findEquipmentById(equipmentId)
                    .orElseThrow(() -> new IllegalArgumentException("Equipment item not found"));
            
            int newTotal = eq.getTotalQuantity() + change;
            int newAvailable = eq.getAvailableQuantity() + change;

            if (newTotal < 0 || newAvailable < 0) {
                throw new IllegalStateException("Stock cannot be negative.");
            }

            eq.setTotalQuantity(newTotal);
            eq.setAvailableQuantity(newAvailable);
            
            User user = UserSession.getLoggedInUser(session);
            String actorEmail = (user != null) ? user.getEmail() : "logistics@safari.lk";
            inventoryService.saveEquipment(eq, actorEmail);
            redirectAttributes.addFlashAttribute("successMessage", "Stock level updated successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/inventory/dashboard";
    }

    @PostMapping("/toggle-status/{id}")
    public String toggleConditionStatus(@PathVariable("id") Long equipmentId, HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            Equipment eq = inventoryService.findEquipmentById(equipmentId)
                    .orElseThrow(() -> new IllegalArgumentException("Equipment item not found"));
            
            if ("GOOD".equals(eq.getConditionStatus())) {
                eq.setConditionStatus("UNDER_REPAIR");
            } else {
                eq.setConditionStatus("GOOD");
            }

            User user = UserSession.getLoggedInUser(session);
            String actorEmail = (user != null) ? user.getEmail() : "logistics@safari.lk";
            inventoryService.saveEquipment(eq, actorEmail);
            redirectAttributes.addFlashAttribute("successMessage", "Equipment condition status updated.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/inventory/dashboard";
    }
}
