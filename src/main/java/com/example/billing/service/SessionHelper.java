package com.example.billing.service;

import com.example.billing.exception.ForbiddenException;
import com.example.billing.exception.UnauthorizedException;
import com.example.billing.model.Role;
import jakarta.servlet.http.HttpSession;

/**
 * Utility methods for reading and validating HttpSession data.
 * Called by every protected service method.
 */
public class SessionHelper {

    public static final String SESSION_USER_ID   = "userId";
    public static final String SESSION_USERNAME  = "username";
    public static final String SESSION_ROLE      = "role";

    /** Returns userId if session is valid, throws 401 otherwise. */
    public static Long requireSession(HttpSession session) {
        Object userId = session.getAttribute(SESSION_USER_ID);
        if (userId == null) {
            throw new UnauthorizedException("Not logged in. Please login first.");
        }
        return (Long) userId;
    }

    /** Returns userId if session is valid AND role matches, throws 403 otherwise. */
    public static Long requireRole(HttpSession session, Role required) {
        Long userId = requireSession(session);
        String roleStr = (String) session.getAttribute(SESSION_ROLE);
        if (!required.name().equals(roleStr)) {
            throw new ForbiddenException("Access denied. Required role: " + required.name());
        }
        return userId;
    }
}
