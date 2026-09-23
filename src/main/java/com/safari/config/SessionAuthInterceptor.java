package com.safari.config;

import com.safari.common.UserSession;
import com.safari.module.user_mgmt.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Set;

@Component
public class SessionAuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();
        String path = uri.startsWith(contextPath) ? uri.substring(contextPath.length()) : uri;

        HttpSession session = request.getSession(false);
        User user = (session != null) ? UserSession.getLoggedInUser(session) : null;

        // 1. If unauthenticated, redirect to login with destination redirect URL
        if (user == null) {
            String target = path;
            if (request.getQueryString() != null && !request.getQueryString().isBlank()) {
                target += "?" + request.getQueryString();
            }
            String encodedTarget = URLEncoder.encode(target, StandardCharsets.UTF_8);
            response.sendRedirect(contextPath + "/login?redirect=" + encodedTarget + "&authRequired=true");
            return false;
        }

        // 2. Role-Based Access Control checks
        String role = user.getRole();

        // Admin Module
        if (path.startsWith("/admin/") && !"ADMIN".equalsIgnoreCase(role)) {
            response.sendRedirect(contextPath + getAuthorizedHome(role) + "?unauthorized=true");
            return false;
        }

        // Tour Operator Package Management
        if ((path.startsWith("/packages/manage") || path.startsWith("/packages/create")
                || path.startsWith("/packages/edit") || path.startsWith("/packages/delete"))
                && !hasAnyRole(role, "TOUR_OPERATOR", "ADMIN")) {
            response.sendRedirect(contextPath + getAuthorizedHome(role) + "?unauthorized=true");
            return false;
        }

        // Allocation Module (Operations Manager)
        if (path.startsWith("/allocation/") && !hasAnyRole(role, "OPERATIONS_MANAGER", "TOUR_OPERATOR", "ADMIN")) {
            response.sendRedirect(contextPath + getAuthorizedHome(role) + "?unauthorized=true");
            return false;
        }

        // Conservation Module (Conservation Officer / DWC)
        if (path.startsWith("/conservation/") && !hasAnyRole(role, "CONSERVATION_OFFICER", "TOUR_OPERATOR", "ADMIN")) {
            response.sendRedirect(contextPath + getAuthorizedHome(role) + "?unauthorized=true");
            return false;
        }

        // Inventory Depot Module (Logistics Staff)
        if (path.startsWith("/inventory/") && !hasAnyRole(role, "LOGISTICS_STAFF", "OPERATIONS_MANAGER", "ADMIN")) {
            response.sendRedirect(contextPath + getAuthorizedHome(role) + "?unauthorized=true");
            return false;
        }

        // Finance Module (Finance Officer & Customer checkout)
        if (path.startsWith("/finance/")) {
            if (path.startsWith("/finance/checkout") || path.startsWith("/finance/pay")) {
                if (!hasAnyRole(role, "FINANCE_OFFICER", "ADMIN", "CUSTOMER")) {
                    response.sendRedirect(contextPath + getAuthorizedHome(role) + "?unauthorized=true");
                    return false;
                }
            } else {
                if (!hasAnyRole(role, "FINANCE_OFFICER", "ADMIN")) {
                    response.sendRedirect(contextPath + getAuthorizedHome(role) + "?unauthorized=true");
                    return false;
                }
            }
        }

        // Staff Booking Management
        if (path.startsWith("/bookings/manage") && !hasAnyRole(role, "ADMIN", "TOUR_OPERATOR", "OPERATIONS_MANAGER")) {
            response.sendRedirect(contextPath + getAuthorizedHome(role) + "?unauthorized=true");
            return false;
        }

        return true;
    }

    private boolean hasAnyRole(String currentRole, String... allowedRoles) {
        if (currentRole == null) return false;
        for (String r : allowedRoles) {
            if (r.equalsIgnoreCase(currentRole)) return true;
        }
        return false;
    }

    private String getAuthorizedHome(String role) {
        if (role == null) return "/";
        switch (role.toUpperCase()) {
            case "ADMIN": return "/admin/dashboard";
            case "TOUR_OPERATOR": return "/packages/manage";
            case "OPERATIONS_MANAGER": return "/allocation/dashboard";
            case "CONSERVATION_OFFICER": return "/conservation/dashboard";
            case "LOGISTICS_STAFF": return "/inventory/dashboard";
            case "FINANCE_OFFICER": return "/finance/dashboard";
            case "CUSTOMER":
            default: return "/bookings/my-bookings";
        }
    }
}
