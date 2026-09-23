package com.safari.module.user_mgmt;

import com.safari.common.ActivityLog;
import com.safari.common.ActivityLogService;
import com.safari.common.UserSession;
import com.safari.module.allocation_mgmt.AllocationService;
import com.safari.module.booking_mgmt.BookingService;
import com.safari.module.finance_mgmt.FinanceService;
import com.safari.module.inventory_mgmt.InventoryService;
import com.safari.module.package_mgmt.SafariPackageService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final ActivityLogService activityLogService;
    private final SafariPackageService packageService;
    private final BookingService bookingService;
    private final AllocationService allocationService;
    private final InventoryService inventoryService;
    private final FinanceService financeService;
    private final DatabaseViewerService databaseViewerService;

    public AdminController(UserService userService,
                           ActivityLogService activityLogService,
                           SafariPackageService packageService,
                           BookingService bookingService,
                           AllocationService allocationService,
                           InventoryService inventoryService,
                           FinanceService financeService,
                           DatabaseViewerService databaseViewerService) {
        this.userService = userService;
        this.activityLogService = activityLogService;
        this.packageService = packageService;
        this.bookingService = bookingService;
        this.allocationService = allocationService;
        this.inventoryService = inventoryService;
        this.financeService = financeService;
        this.databaseViewerService = databaseViewerService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        User user = UserSession.getLoggedInUser(session);
        if (user == null) {
            return "redirect:/login?redirect=/admin/dashboard";
        }
        if (!"ADMIN".equalsIgnoreCase(user.getRole())) {
            return "redirect:/";
        }

        model.addAttribute("currentUser", user);
        model.addAttribute("currentRole", user.getRole());

        // Cross-module metrics
        model.addAttribute("totalPackages", packageService.getAllPackages().size());
        model.addAttribute("totalBookings", bookingService.getAllBookings().size());
        model.addAttribute("totalGuides", allocationService.getAllGuides().size());
        model.addAttribute("totalVehicles", allocationService.getAllVehicles().size());
        model.addAttribute("lowStockItems", inventoryService.getLowStockEquipment().size());
        model.addAttribute("revenueSummary", financeService.getRevenueReport());
        model.addAttribute("recentLogs", activityLogService.getRecentLogs());

        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String userList(Model model, HttpSession session) {
        User user = UserSession.getLoggedInUser(session);
        if (user == null) {
            return "redirect:/login?redirect=/admin/users";
        }
        if (!"ADMIN".equalsIgnoreCase(user.getRole())) {
            return "redirect:/";
        }

        model.addAttribute("currentUser", user);
        model.addAttribute("currentRole", user.getRole());
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("newUser", new User());
        return "admin/users";
    }

    @PostMapping("/users/create")
    public String createUser(@Valid @ModelAttribute("newUser") User newUser,
                             BindingResult bindingResult,
                             HttpSession session,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        User admin = UserSession.getLoggedInUser(session);
        if (admin == null || !"ADMIN".equalsIgnoreCase(admin.getRole())) {
            return "redirect:/login";
        }

        if (!newUser.getEmail().matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) {
            bindingResult.rejectValue("email", "error.newUser", "Valid email address required (e.g. name@gmail.com)");
        }

        if (!newUser.getPhone().matches("^0[0-9]{9}$|^[0-9]{10}$")) {
            bindingResult.rejectValue("phone", "error.newUser", "Phone must be exactly 10 digits");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("currentUser", admin);
            model.addAttribute("currentRole", admin.getRole());
            model.addAttribute("users", userService.getAllUsers());
            return "admin/users";
        }

        try {
            userService.registerUser(newUser);
            redirectAttributes.addFlashAttribute("successMessage", "New staff account (" + newUser.getRole() + ") added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/admin/users";
    }

    @PostMapping("/users/edit/{id}")
    public String editUser(@PathVariable("id") Long id,
                           @RequestParam("fullName") String fullName,
                           @RequestParam("role") String role,
                           HttpSession session,
                           RedirectAttributes redirectAttributes) {
        User admin = UserSession.getLoggedInUser(session);
        if (admin == null || !"ADMIN".equalsIgnoreCase(admin.getRole())) {
            return "redirect:/login";
        }

        try {
            User user = userService.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
            user.setFullName(fullName);
            user.setRole(role);
            userService.updateUser(user);
            redirectAttributes.addFlashAttribute("successMessage", "User updated successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/users";
    }
    
    @GetMapping("/users/edit/{id}")
    public String editUserGetFallback() {
        return "redirect:/admin/users";
    }

    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        User admin = UserSession.getLoggedInUser(session);
        if (admin == null || !"ADMIN".equalsIgnoreCase(admin.getRole())) {
            return "redirect:/login";
        }
        
        if (admin.getId().equals(id)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Cannot delete your own admin account.");
            return "redirect:/admin/users";
        }

        try {
            userService.deleteUser(id, admin.getEmail());
            redirectAttributes.addFlashAttribute("successMessage", "User deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUserGetFallback() {
        return "redirect:/admin/users";
    }

    @GetMapping("/activity-logs")
    public String viewActivityLogs(Model model, HttpSession session) {
        User user = UserSession.getLoggedInUser(session);
        if (user == null) {
            return "redirect:/login?redirect=/admin/activity-logs";
        }
        if (!"ADMIN".equalsIgnoreCase(user.getRole())) {
            return "redirect:/";
        }

        model.addAttribute("currentUser", user);
        model.addAttribute("currentRole", user.getRole());
        List<ActivityLog> logs = activityLogService.getRecentLogs();
        model.addAttribute("logs", logs);
        return "admin/activity_logs";
    }

    @GetMapping("/database")
    public String viewDatabase(@RequestParam(value = "table", required = false, defaultValue = "safari_packages") String table,
                               Model model,
                               HttpSession session) {
        User user = UserSession.getLoggedInUser(session);
        if (user == null) {
            return "redirect:/login?redirect=/admin/database";
        }
        if (!"ADMIN".equalsIgnoreCase(user.getRole())) {
            return "redirect:/";
        }

        model.addAttribute("currentUser", user);
        model.addAttribute("currentRole", user.getRole());
        model.addAttribute("selectedTable", table);
        model.addAttribute("tables", databaseViewerService.getAllTableSummaries());
        model.addAttribute("activeTableData", databaseViewerService.getTableData(table));
        model.addAttribute("dbMetadata", databaseViewerService.getDatabaseMetadata());
        return "admin/database";
    }
}
