package com.safari.module.booking_mgmt;

import com.safari.common.UserSession;
import com.safari.module.allocation_mgmt.AllocationService;
import com.safari.module.package_mgmt.SafariPackage;
import com.safari.module.package_mgmt.SafariPackageService;
import com.safari.module.user_mgmt.User;
import com.safari.module.inventory_mgmt.InventoryService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final SafariPackageService packageService;
    private final AllocationService allocationService;
    private final InventoryService inventoryService;

    public BookingController(BookingService bookingService,
                             SafariPackageService packageService,
                             AllocationService allocationService,
                             InventoryService inventoryService) {
        this.bookingService = bookingService;
        this.packageService = packageService;
        this.allocationService = allocationService;
        this.inventoryService = inventoryService;
    }

    @GetMapping("/new/{packageId}")
    public String showBookingForm(@PathVariable("packageId") Long packageId,
                                  Model model, HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        User user = UserSession.getLoggedInUser(session);
        if (user == null) {
            redirectAttributes.addFlashAttribute("infoMessage", "Please sign in to proceed with booking this expedition.");
            return "redirect:/login?redirect=/bookings/new/" + packageId;
        }

        SafariPackage pkg = packageService.findById(packageId)
                .orElseThrow(() -> new IllegalArgumentException("Safari Package not found"));

        Booking booking = new Booking();
        booking.setSafariPackage(pkg);
        booking.setTripDate(LocalDate.now().plusDays(2));

        booking.setCustomerId(user.getId());
        booking.setCustomerName(user.getFullName());
        booking.setCustomerEmail(user.getEmail());
        booking.setCustomerPhone(user.getPhone());

        model.addAttribute("pkg", pkg);
        model.addAttribute("booking", booking);
        model.addAttribute("currentUser", user);
        model.addAttribute("currentRole", UserSession.getCurrentRole(session));
        return "bookings/form";
    }

    @PostMapping("/create")
    public String createBooking(@RequestParam("packageId") Long packageId,
                                @Valid @ModelAttribute("booking") Booking booking,
                                BindingResult bindingResult,
                                HttpSession session,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        User user = UserSession.getLoggedInUser(session);
        if (user == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Your session has expired. Please sign in to book your expedition.");
            return "redirect:/login?redirect=/bookings/new/" + packageId;
        }

        SafariPackage pkg = packageService.findById(packageId)
                .orElseThrow(() -> new IllegalArgumentException("Package not found"));

        // Custom strict validation for email format (especially @gmail.com or standard domain)
        if (!booking.getCustomerEmail().matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) {
            bindingResult.rejectValue("customerEmail", "error.booking", "Valid email is required (e.g. name@gmail.com)");
        }

        // Custom strict validation for 10-digit phone number
        if (!booking.getCustomerPhone().matches("^0[0-9]{9}$|^[0-9]{10}$")) {
            bindingResult.rejectValue("customerPhone", "error.booking", "Phone number must be exactly 10 digits (e.g. 0771234567)");
        }

        if (booking.getParticipantCount() > pkg.getMaxGroupSize()) {
            bindingResult.rejectValue("participantCount", "error.booking",
                    "Participant count exceeds maximum capacity (" + pkg.getMaxGroupSize() + ")");
        }

        if (booking.getTripDate() != null && booking.getTripDate().isBefore(LocalDate.now())) {
            bindingResult.rejectValue("tripDate", "error.booking", "Trip date cannot be in the past");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("pkg", pkg);
            model.addAttribute("currentUser", user);
            model.addAttribute("currentRole", UserSession.getCurrentRole(session));
            return "bookings/form";
        }

        booking.setCustomerId(user.getId());
        booking.setCustomerEmail(user.getEmail());
        booking.setCustomerName(user.getFullName());
        String actorEmail = user.getEmail();

        try {
            Booking saved = bookingService.createBooking(booking, packageId, actorEmail);
            redirectAttributes.addFlashAttribute("successMessage", "Booking created successfully! Reference: " + saved.getBookingReference());
            return "redirect:/bookings/confirmation/" + saved.getBookingReference();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/bookings/new/" + packageId;
        }
    }

    @GetMapping("/confirmation/{reference}")
    public String showConfirmation(@PathVariable("reference") String reference, Model model, HttpSession session) {
        Booking booking = bookingService.findByReference(reference)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found: " + reference));

        model.addAttribute("booking", booking);
        model.addAttribute("allocation", allocationService.findAllocationByBookingId(booking.getId()).orElse(null));
        model.addAttribute("equipmentList", inventoryService.getAllEquipment());
        model.addAttribute("rentedEquipment", inventoryService.getAllocationsForBooking(booking.getId()));
        model.addAttribute("currentUser", UserSession.getLoggedInUser(session));
        model.addAttribute("currentRole", UserSession.getCurrentRole(session));
        return "bookings/confirmation";
    }

    @PostMapping("/rent-equipment")
    public String rentEquipment(@RequestParam("bookingId") Long bookingId,
                                @RequestParam("equipmentId") Long equipmentId,
                                @RequestParam("quantity") int quantity,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        User user = UserSession.getLoggedInUser(session);
        String actorEmail = (user != null) ? user.getEmail() : "customer@safari.lk";

        try {
            inventoryService.requestEquipment(equipmentId, bookingId, quantity, "Customer request via voucher", actorEmail);
            redirectAttributes.addFlashAttribute("successMessage", "Equipment request submitted and pending approval.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        Booking booking = bookingService.findById(bookingId).orElseThrow();
        return "redirect:/bookings/confirmation/" + booking.getBookingReference();
    }

    @GetMapping("/my-bookings")
    public String myBookings(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        User user = UserSession.getLoggedInUser(session);
        if (user == null) {
            redirectAttributes.addFlashAttribute("infoMessage", "Please sign in to view your bookings.");
            return "redirect:/login?redirect=/bookings/my-bookings";
        }

        // Query by userId (always set on booking create) — email can be null in some DB rows
        List<Booking> bookings = bookingService.getCustomerBookingsById(user.getId());
        // Fallback: also include bookings matched by email (union)
        List<Booking> byEmail = bookingService.getCustomerBookings(user.getEmail());
        byEmail.stream()
               .filter(b -> bookings.stream().noneMatch(eb -> eb.getId().equals(b.getId())))
               .forEach(bookings::add);
        bookings.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
        model.addAttribute("bookings", bookings);
        model.addAttribute("currentUser", user);
        model.addAttribute("currentRole", user.getRole());
        return "bookings/my_bookings";
    }

    @GetMapping("/manage")
    public String manageBookings(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        User user = UserSession.getLoggedInUser(session);
        if (user == null) {
            redirectAttributes.addFlashAttribute("infoMessage", "Please sign in with staff credentials.");
            return "redirect:/login?redirect=/bookings/manage";
        }
        if (!"TOUR_OPERATOR".equalsIgnoreCase(user.getRole()) &&
                !"ADMIN".equalsIgnoreCase(user.getRole()) &&
                !"OPERATIONS_MANAGER".equalsIgnoreCase(user.getRole())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Access restricted to authorized operations staff.");
            return "redirect:/bookings/my-bookings";
        }

        model.addAttribute("bookings", bookingService.getAllBookings());
        model.addAttribute("currentUser", user);
        model.addAttribute("currentRole", user.getRole());
        return "bookings/manage";
    }

    @PostMapping("/cancel/{id}")
    public String cancelBooking(@PathVariable("id") Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        User user = UserSession.getLoggedInUser(session);
        String actorEmail = (user != null) ? user.getEmail() : "customer@safari.lk";

        try {
            bookingService.cancelBooking(id, actorEmail);
            redirectAttributes.addFlashAttribute("successMessage", "Booking has been cancelled. Historical record retained.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return (user != null && "CUSTOMER".equalsIgnoreCase(user.getRole())) ? "redirect:/bookings/my-bookings" : "redirect:/bookings/manage";
    }

    @PostMapping("/update/{id}")
    public String updateBooking(@PathVariable("id") Long id,
                                @RequestParam("tripDate") LocalDate tripDate,
                                @RequestParam("participantCount") int participantCount,
                                @RequestParam("specialRequests") String specialRequests,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        User user = UserSession.getLoggedInUser(session);
        String actorEmail = (user != null) ? user.getEmail() : "customer@safari.lk";

        try {
            bookingService.updateBooking(id, tripDate, participantCount, specialRequests, actorEmail);
            redirectAttributes.addFlashAttribute("successMessage", "Booking details successfully updated!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return (user != null && "CUSTOMER".equalsIgnoreCase(user.getRole())) ? "redirect:/bookings/my-bookings" : "redirect:/bookings/manage";
    }
}
