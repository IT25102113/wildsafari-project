package com.safari.module.package_mgmt;

import com.safari.common.UserSession;
import com.safari.module.booking_mgmt.BookingService;
import com.safari.module.user_mgmt.User;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/packages")
public class SafariPackageController {

    private final SafariPackageService packageService;
    private final BookingService bookingService;

    public SafariPackageController(SafariPackageService packageService, BookingService bookingService) {
        this.packageService = packageService;
        this.bookingService = bookingService;
    }

    @GetMapping
    public String browsePackages(@RequestParam(value = "park", required = false) String park,
                                 Model model, HttpSession session) {
        List<SafariPackage> packages;
        if (park != null && !park.isBlank()) {
            packages = packageService.getActivePackages().stream()
                    .filter(p -> p.getNationalPark().equalsIgnoreCase(park))
                    .toList();
        } else {
            packages = packageService.getActivePackages();
        }
        model.addAttribute("packages", packages);
        model.addAttribute("selectedPark", park);
        model.addAttribute("currentUser", UserSession.getLoggedInUser(session));
        model.addAttribute("currentRole", UserSession.getCurrentRole(session));
        return "packages/browse";
    }

    @GetMapping("/{id}")
    public String viewPackageDetails(@PathVariable("id") Long id, Model model, HttpSession session) {
        SafariPackage pkg = packageService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Package not found"));
        model.addAttribute("pkg", pkg);
        model.addAttribute("currentUser", UserSession.getLoggedInUser(session));
        model.addAttribute("currentRole", UserSession.getCurrentRole(session));
        return "packages/details";
    }

    @GetMapping("/manage")
    public String managePackages(Model model, HttpSession session) {
        User user = UserSession.getLoggedInUser(session);
        if (user == null) {
            return "redirect:/login?redirect=/packages/manage";
        }
        if (!"TOUR_OPERATOR".equalsIgnoreCase(user.getRole()) && !"ADMIN".equalsIgnoreCase(user.getRole())) {
            return "redirect:/";
        }

        List<SafariPackage> allPackages = packageService.getAllPackages();
        Map<Long, Long> bookingCounts = new HashMap<>();
        for (SafariPackage p : allPackages) {
            bookingCounts.put(p.getId(), bookingService.getBookingCountForPackage(p.getId()));
        }

        model.addAttribute("currentUser", user);
        model.addAttribute("currentRole", user.getRole());
        model.addAttribute("packages", allPackages);
        model.addAttribute("bookingCounts", bookingCounts);
        return "packages/manage";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model, HttpSession session) {
        User user = UserSession.getLoggedInUser(session);
        if (user == null) {
            return "redirect:/login?redirect=/packages/create";
        }
        if (!"TOUR_OPERATOR".equalsIgnoreCase(user.getRole()) && !"ADMIN".equalsIgnoreCase(user.getRole())) {
            return "redirect:/";
        }

        model.addAttribute("currentUser", user);
        model.addAttribute("currentRole", user.getRole());
        model.addAttribute("safariPackage", new SafariPackage());
        return "packages/form";
    }

    @PostMapping("/save")
    public String savePackage(@Valid @ModelAttribute("safariPackage") SafariPackage safariPackage,
                              BindingResult bindingResult,
                              @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                              HttpSession session,
                              RedirectAttributes redirectAttributes,
                              Model model) {
        User user = UserSession.getLoggedInUser(session);
        String actorEmail = (user != null) ? user.getEmail() : "operator@safari.lk";

        if (bindingResult.hasErrors()) {
            model.addAttribute("currentUser", user);
            model.addAttribute("currentRole", UserSession.getCurrentRole(session));
            return "packages/form";
        }

        try {
            packageService.savePackage(safariPackage, imageFile, actorEmail);
            redirectAttributes.addFlashAttribute("successMessage", "Safari package saved successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to save package: " + e.getMessage());
        }

        return "redirect:/packages/manage";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") Long id, Model model, HttpSession session) {
        SafariPackage pkg = packageService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Package not found"));
        model.addAttribute("currentUser", UserSession.getLoggedInUser(session));
        model.addAttribute("currentRole", UserSession.getCurrentRole(session));
        model.addAttribute("safariPackage", pkg);
        return "packages/form";
    }

    @PostMapping("/discontinue/{id}")
    public String discontinuePackage(@PathVariable("id") Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        User user = UserSession.getLoggedInUser(session);
        String actorEmail = (user != null) ? user.getEmail() : "operator@safari.lk";
        packageService.discontinuePackage(id, actorEmail);
        redirectAttributes.addFlashAttribute("successMessage", "Package discontinued successfully. Historical booking records preserved.");
        return "redirect:/packages/manage";
    }

    @PostMapping("/toggle-status/{id}")
    public String toggleStatus(@PathVariable("id") Long id, @RequestParam("status") String status, HttpSession session, RedirectAttributes redirectAttributes) {
        User user = UserSession.getLoggedInUser(session);
        String actorEmail = (user != null) ? user.getEmail() : "operator@safari.lk";
        packageService.toggleStatus(id, status, actorEmail);
        redirectAttributes.addFlashAttribute("successMessage", "Package status updated to " + status + ".");
        return "redirect:/packages/manage";
    }

    @PostMapping("/delete/{id}")
    public String deletePackage(@PathVariable("id") Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        User user = UserSession.getLoggedInUser(session);
        String actorEmail = (user != null) ? user.getEmail() : "operator@safari.lk";
        packageService.deletePackage(id, actorEmail);
        redirectAttributes.addFlashAttribute("successMessage", "Package removed.");
        return "redirect:/packages/manage";
    }
}
