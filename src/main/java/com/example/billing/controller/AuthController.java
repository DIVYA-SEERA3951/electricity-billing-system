package com.example.billing.controller;

import com.example.billing.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * POST /api/register
     * Body: { username, password, role, name?, email?, address? }
     * role must be ADMIN or CUSTOMER.
     * CUSTOMER registrations require name, email, address.
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        String role     = body.get("role");
        String name     = body.get("name");
        String email    = body.get("email");
        String address  = body.get("address");

        if (username == null || username.isBlank()) throw new IllegalArgumentException("Username is required");
        if (password == null || password.isBlank()) throw new IllegalArgumentException("Password is required");
        if (role     == null || role.isBlank())     throw new IllegalArgumentException("Role is required (ADMIN or CUSTOMER)");

        Map<String, Object> res = authService.register(username, password, role, name, email, address);
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    /**
     * POST /api/login
     * Body: { username, password }
     * Sets HttpSession on success.
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> body,
                                                      HttpSession session) {
        String username = body.get("username");
        String password = body.get("password");
        if (username == null || username.isBlank()) throw new IllegalArgumentException("Username is required");
        if (password == null || password.isBlank()) throw new IllegalArgumentException("Password is required");

        Map<String, Object> res = authService.login(username, password, session);
        return ResponseEntity.ok(res);
    }

    /**
     * POST /api/logout
     * Invalidates the session.
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpSession session) {
        Map<String, String> res = authService.logout(session);
        return ResponseEntity.ok(res);
    }

    /**
     * GET /api/auth/check
     * Returns current session info or 401.
     * Frontend calls this on every protected page load.
     */
    @GetMapping("/auth/check")
    public ResponseEntity<Map<String, Object>> checkSession(HttpSession session) {
        Map<String, Object> res = authService.checkSession(session);
        return ResponseEntity.ok(res);
    }
}
