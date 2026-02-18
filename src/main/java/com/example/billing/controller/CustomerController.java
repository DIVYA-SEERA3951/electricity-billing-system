package com.example.billing.controller;

import com.example.billing.model.Bill;
import com.example.billing.model.Customer;
import com.example.billing.service.CustomerService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping("/profile")
    public ResponseEntity<Customer> getProfile(HttpSession session) {
        return ResponseEntity.ok(customerService.getProfile(session));
    }

    @GetMapping("/bills")
    public ResponseEntity<List<Bill>> getMyBills(HttpSession session) {
        return ResponseEntity.ok(customerService.getMyBills(session));
    }
}
