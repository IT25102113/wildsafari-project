package com.safari.module.user_mgmt;

import com.safari.common.UserSession;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String showLoginForm(@RequestParam(value = "role", required = false) String requestedRole,
                                @RequestParam(value = "redirect", required = false) String redirectUrl,
                                @RequestParam(value = "authRequired", required = false) Boolean authRequired,
                                Model model, HttpSession session) {
        if (UserSession.isLoggedIn(session)) {
            if (redirectUrl != null && !redirectUrl.isBlank() && redirectUrl.startsWith("/")) {
                return "redirect:" + redirectUrl;
            }
            return redirectToRoleDashboard(UserSession.getCurrentRole(session));
        }

        if (Boolean.TRUE.equals(authRequired)) {
            model.addAttribute("infoMessage", "Please sign in to your account to complete your booking or access authorized features.");
        }

        model.addAttribute("requestedRole", requestedRole != null ? requestedRole : "CUSTOMER");
        model.addAttribute("redirectUrl", redirectUrl);
        return "login";
    }

    @PostMapping("/login")
    public String handleLogin(@RequestParam("email") String email,
                              @RequestParam("password") String password,
                              @RequestParam(value = "redirect", required = false) String redirectUrl,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {
        Optional<User> userOpt = userService.authenticate(email, password);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            UserSession.setLoggedInUser(session, user);
            redirectAttributes.addFlashAttribute("successMessage", "Welcome back, " + user.getFullName() + "!");

            // If a redirect URL was provided (e.g. /bookings/new/1), honor it!
            if (redirectUrl != null && !redirectUrl.isBlank() && redirectUrl.startsWith("/")) {
                return "redirect:" + redirectUrl;
            }
            return redirectToRoleDashboard(user.getRole());
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid email or password. Please verify credentials.");
            if (redirectUrl != null && !redirectUrl.isBlank()) {
                return "redirect:/login?redirect=" + redirectUrl;
            }
            return "redirect:/login";
        }
    }

    @GetMapping("/register")
    public String showRegisterForm(@RequestParam(value = "redirect", required = false) String redirectUrl,
                                   Model model, HttpSession session) {
        if (UserSession.isLoggedIn(session)) {
            if (redirectUrl != null && !redirectUrl.isBlank() && redirectUrl.startsWith("/")) {
                return "redirect:" + redirectUrl;
            }
            return "redirect:/";
        }
        User newUser = new User();
        newUser.setRole("CUSTOMER");
        model.addAttribute("user", newUser);
        model.addAttribute("redirectUrl", redirectUrl);
        return "register";
    }

    @PostMapping("/register")
    public String handleRegister(@Valid @ModelAttribute("user") User user,
                                 BindingResult bindingResult,
                                 @RequestParam(value = "redirect", required = false) String redirectUrl,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {
        user.setRole("CUSTOMER");

        // Strict Gmail / Email check
        if (user.getEmail() == null || !user.getEmail().matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) {
            bindingResult.rejectValue("email", "error.user", "Email must be in a valid format (e.g. name@gmail.com)");
        } else if (userService.findByEmail(user.getEmail().trim()).isPresent()) {
            bindingResult.rejectValue("email", "error.user", "An account with email " + user.getEmail() + " already exists. Please sign in.");
        }

        // Strict 10-digit phone check
        if (user.getPhone() == null || !user.getPhone().matches("^[0-9]{10}$")) {
            bindingResult.rejectValue("phone", "error.user", "Phone number must be exactly 10 digits (e.g. 0771234567)");
        }

        // Password minimum length check
        if (user.getPassword() == null || user.getPassword().length() < 4) {
            bindingResult.rejectValue("password", "error.user", "Password must be at least 4 characters long.");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("redirectUrl", redirectUrl);
            return "register";
        }

        try {
            User saved = userService.registerUser(user);
            UserSession.setLoggedInUser(session, saved);
            redirectAttributes.addFlashAttribute("successMessage", "Account registered successfully! Welcome to Wildlife Safari, " + saved.getFullName() + ".");

            if (redirectUrl != null && !redirectUrl.isBlank() && redirectUrl.startsWith("/")) {
                return "redirect:" + redirectUrl;
            }
            return "redirect:/";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("redirectUrl", redirectUrl);
            return "register";
        }
    }

    @GetMapping("/logout")
    public String handleLogout(HttpSession session, RedirectAttributes redirectAttributes) {
        UserSession.logout(session);
        redirectAttributes.addFlashAttribute("successMessage", "You have been logged out successfully.");
        return "redirect:/login";
    }

    /**
     * Viva Demo Quick-Switcher:
     * Instantly switches the active session to the selected role with realistic pre-configured accounts.
     */
    @GetMapping("/demo/switch-role")
    public String switchDemoRole(@RequestParam("role") String role, HttpSession session, RedirectAttributes redirectAttributes) {
        String targetEmail;
        switch (role.toUpperCase()) {
            case "ADMIN":
                targetEmail = "admin@safari.lk";
                break;
            case "TOUR_OPERATOR":
                targetEmail = "operator@safari.lk";
                break;
            case "OPERATIONS_MANAGER":
                targetEmail = "ops@safari.lk";
                break;
            case "CONSERVATION_OFFICER":
                targetEmail = "ranger@safari.lk";
                break;
            case "LOGISTICS_STAFF":
                targetEmail = "logistics@safari.lk";
                break;
            case "FINANCE_OFFICER":
                targetEmail = "finance@safari.lk";
                break;
            case "CUSTOMER":
            default:
                targetEmail = "kavinda.perera@gmail.com";
                break;
        }

        userService.findByEmail(targetEmail).ifPresent(user -> {
            UserSession.setLoggedInUser(session, user);
            redirectAttributes.addFlashAttribute("successMessage", "Switched role to: " + user.getRole() + " (" + user.getFullName() + ")");
        });

        return redirectToRoleDashboard(role.toUpperCase());
    }

    private String redirectToRoleDashboard(String role) {
        switch (role) {
            case "ADMIN":
                return "redirect:/admin/dashboard";
            case "TOUR_OPERATOR":
                return "redirect:/packages/manage";
            case "OPERATIONS_MANAGER":
                return "redirect:/allocation/dashboard";
            case "CONSERVATION_OFFICER":
                return "redirect:/conservation/dashboard";
            case "LOGISTICS_STAFF":
                return "redirect:/inventory/dashboard";
            case "FINANCE_OFFICER":
                return "redirect:/finance/dashboard";
            case "CUSTOMER":
            default:
                return "redirect:/";
        }
    }
}
