package com.example.billing.service;

import com.example.billing.model.Customer;
import com.example.billing.model.Role;
import com.example.billing.model.User;
import com.example.billing.repository.CustomerRepository;
import com.example.billing.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    /**
     * Register a new user.
     * If role=CUSTOMER, a Customer record is also created automatically.
     */
    public Map<String, Object> register(String username, String password, String roleName,
                                        String name, String email, String address) {

        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already taken: " + username);
        }

        Role role;
        try {
            role = Role.valueOf(roleName.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role. Use ADMIN or CUSTOMER.");
        }

        User user = new User(username, password, role);
        userRepository.save(user);

        // If registering as CUSTOMER, also create a Customer profile
        if (role == Role.CUSTOMER) {
            if (name == null || name.isBlank()) throw new IllegalArgumentException("Name is required for CUSTOMER registration.");
            if (email == null || email.isBlank()) throw new IllegalArgumentException("Email is required for CUSTOMER registration.");
            if (address == null || address.isBlank()) throw new IllegalArgumentException("Address is required for CUSTOMER registration.");
            if (customerRepository.existsByEmail(email)) throw new IllegalArgumentException("Email already registered: " + email);

            Customer customer = new Customer(name, email, address, user);
            customerRepository.save(customer);
        }

        Map<String, Object> res = new HashMap<>();
        res.put("message", "Registration successful");
        res.put("username", username);
        res.put("role", role.name());
        return res;
    }

    /**
     * Login: validate credentials, populate HttpSession.
     */
    public Map<String, Object> login(String username, String password, HttpSession session) {
        Optional<User> opt = userRepository.findByUsername(username);
        if (opt.isEmpty() || !opt.get().getPassword().equals(password)) {
            throw new IllegalArgumentException("Invalid username or password.");
        }
        User user = opt.get();

        session.setAttribute(SessionHelper.SESSION_USER_ID,  user.getId());
        session.setAttribute(SessionHelper.SESSION_USERNAME, user.getUsername());
        session.setAttribute(SessionHelper.SESSION_ROLE,     user.getRole().name());

        Map<String, Object> res = new HashMap<>();
        res.put("message", "Login successful");
        res.put("username", user.getUsername());
        res.put("role", user.getRole().name());
        return res;
    }

    /** Invalidate session (logout). */
    public Map<String, String> logout(HttpSession session) {
        session.invalidate();
        Map<String, String> res = new HashMap<>();
        res.put("message", "Logged out successfully");
        return res;
    }

    /** Check current session status — used by frontend on page load. */
    @Transactional(readOnly = true)
    public Map<String, Object> checkSession(HttpSession session) {
        Object userId = session.getAttribute(SessionHelper.SESSION_USER_ID);
        if (userId == null) {
            throw new com.example.billing.exception.UnauthorizedException("No active session.");
        }
        Map<String, Object> res = new HashMap<>();
        res.put("loggedIn",  true);
        res.put("username",  session.getAttribute(SessionHelper.SESSION_USERNAME));
        res.put("role",      session.getAttribute(SessionHelper.SESSION_ROLE));
        return res;
    }
}
