package com.safari.module.user_mgmt;

import com.safari.common.UserSession;
import com.safari.module.package_mgmt.SafariPackage;
import com.safari.module.package_mgmt.SafariPackageService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    private final SafariPackageService packageService;

    public HomeController(SafariPackageService packageService) {
        this.packageService = packageService;
    }

    @GetMapping("/")
    public String index(Model model, HttpSession session) {
        List<SafariPackage> activePackages = packageService.getActivePackages();
        model.addAttribute("packages", activePackages);
        model.addAttribute("currentUser", UserSession.getLoggedInUser(session));
        model.addAttribute("currentRole", UserSession.getCurrentRole(session));
        return "index";
    }

    @GetMapping("/about")
    public String about(Model model, HttpSession session) {
        model.addAttribute("currentUser", UserSession.getLoggedInUser(session));
        model.addAttribute("currentRole", UserSession.getCurrentRole(session));
        return "about";
    }
}
