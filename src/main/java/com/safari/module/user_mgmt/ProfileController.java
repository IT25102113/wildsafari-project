package com.safari.module.user_mgmt;

import com.safari.common.UserSession;
import com.safari.module.booking_mgmt.BookingService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;
    private final BookingService bookingService;

    public ProfileController(UserService userService, BookingService bookingService) {
        this.userService = userService;
        this.bookingService = bookingService;
    }

    @GetMapping
    public String viewProfile(Model model, HttpSession session) {
        User sessionUser = UserSession.getLoggedInUser(session);
        if (sessionUser == null) {
            return "redirect:/login?redirect=/profile";
        }

        // Always fetch fresh entity from database
        User freshUser = userService.findById(sessionUser.getId()).orElse(sessionUser);
        UserSession.setLoggedInUser(session, freshUser);

        model.addAttribute("user", freshUser);
        model.addAttribute("currentUser", freshUser);
        model.addAttribute("currentRole", freshUser.getRole());

        // Helpful stats
        int bookingCount = 0;
        if ("CUSTOMER".equalsIgnoreCase(freshUser.getRole())) {
            bookingCount = bookingService.getCustomerBookings(freshUser.getEmail()).size();
        }
        model.addAttribute("bookingCount", bookingCount);

        return "users/profile";
    }

    @PostMapping("/update")
    public String updateProfile(@RequestParam("fullName") String fullName,
                                @RequestParam("phone") String phone,
                                @RequestParam(value = "region", required = false) String region,
                                @RequestParam(value = "bio", required = false) String bio,
                                @RequestParam(value = "newPassword", required = false) String newPassword,
                                @RequestParam(value = "confirmPassword", required = false) String confirmPassword,
                                @RequestParam(value = "profilePictureFile", required = false) MultipartFile profilePictureFile,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        User sessionUser = UserSession.getLoggedInUser(session);
        if (sessionUser == null) {
            return "redirect:/login";
        }

        // Phone validation (10 digits)
        if (phone != null && !phone.isBlank()) {
            String trimmedPhone = phone.trim();
            if (!trimmedPhone.matches("^0[0-9]{9}$|^[0-9]{10}$")) {
                redirectAttributes.addFlashAttribute("errorMessage", "Phone number must be exactly 10 digits (e.g. 0771234567)");
                return "redirect:/profile";
            }
        }

        // Password change validation
        if (newPassword != null && !newPassword.isBlank()) {
            if (newPassword.length() < 5) {
                redirectAttributes.addFlashAttribute("errorMessage", "New password must be at least 5 characters long.");
                return "redirect:/profile";
            }
            if (!newPassword.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("errorMessage", "New password and Confirm password do not match.");
                return "redirect:/profile";
            }
        }

        try {
            User updated = userService.updateProfile(
                    sessionUser.getId(),
                    fullName,
                    phone,
                    region,
                    bio,
                    newPassword,
                    profilePictureFile
            );

            // Sync updated user to session
            UserSession.setLoggedInUser(session, updated);
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update profile: " + e.getMessage());
        }

        return "redirect:/profile";
    }
}
