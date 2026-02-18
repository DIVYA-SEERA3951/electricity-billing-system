package com.example.billing.controller;

import com.example.billing.model.Bill;
import com.example.billing.model.Customer;
import com.example.billing.service.AdminService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    // ---- Customer Endpoints ----

    @PostMapping("/customers")
    public ResponseEntity<Customer> addCustomer(@RequestBody Map<String, String> body,
                                                 HttpSession session) {
        String name    = body.get("name");
        String email   = body.get("email");
        String address = body.get("address");
        if (name    == null || name.isBlank())    throw new IllegalArgumentException("Name is required");
        if (email   == null || email.isBlank())   throw new IllegalArgumentException("Email is required");
        if (address == null || address.isBlank()) throw new IllegalArgumentException("Address is required");
        Customer customer = adminService.addCustomer(session, name, email, address);
        return new ResponseEntity<>(customer, HttpStatus.CREATED);
    }

    @GetMapping("/customers")
    public ResponseEntity<List<Customer>> getAllCustomers(HttpSession session) {
        return ResponseEntity.ok(adminService.getAllCustomers(session));
    }

    @DeleteMapping("/customers/{id}")
    public ResponseEntity<Map<String, String>> deleteCustomer(@PathVariable Long id,
                                                               HttpSession session) {
        adminService.deleteCustomer(session, id);
        return ResponseEntity.ok(Map.of("message", "Customer deleted successfully"));
    }

    // ---- Bill Endpoints ----

    @PostMapping("/bills")
    public ResponseEntity<Bill> generateBill(@RequestBody Map<String, Object> body,
                                              HttpSession session) {
        Long customerId;
        Double units;
        try {
            customerId = Long.parseLong(body.get("customerId").toString());
            units      = Double.parseDouble(body.get("unitsConsumed").toString());
        } catch (Exception e) {
            throw new IllegalArgumentException("customerId and unitsConsumed are required numeric fields.");
        }
        Bill bill = adminService.generateBill(session, customerId, units);
        return new ResponseEntity<>(bill, HttpStatus.CREATED);
    }

    @GetMapping("/bills")
    public ResponseEntity<List<Bill>> getAllBills(HttpSession session) {
        return ResponseEntity.ok(adminService.getAllBills(session));
    }
}
