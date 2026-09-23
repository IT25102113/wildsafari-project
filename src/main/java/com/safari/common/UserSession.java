package com.safari.common;

import com.safari.module.user_mgmt.User;
import jakarta.servlet.http.HttpSession;

public class UserSession {

    public static final String SESSION_USER_KEY = "CURRENT_USER";

    public static void setLoggedInUser(HttpSession session, User user) {
        session.setAttribute(SESSION_USER_KEY, user);
    }

    public static User getLoggedInUser(HttpSession session) {
        Object obj = session.getAttribute(SESSION_USER_KEY);
        return (obj instanceof User) ? (User) obj : null;
    }

    public static boolean isLoggedIn(HttpSession session) {
        return getLoggedInUser(session) != null;
    }

    public static String getCurrentRole(HttpSession session) {
        User user = getLoggedInUser(session);
        return (user != null) ? user.getRole() : "GUEST";
    }

    public static void logout(HttpSession session) {
        session.removeAttribute(SESSION_USER_KEY);
        session.invalidate();
    }
}
