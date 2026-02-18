package com.example.billing.service;

import com.example.billing.exception.ResourceNotFoundException;
import com.example.billing.model.Bill;
import com.example.billing.model.Customer;
import com.example.billing.model.Role;
import com.example.billing.repository.BillRepository;
import com.example.billing.repository.CustomerRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class AdminService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private BillRepository billRepository;

    // ---- Customers ----

    public Customer addCustomer(HttpSession session, String name, String email, String address) {
        SessionHelper.requireRole(session, Role.ADMIN);
        if (customerRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already registered: " + email);
        }
        Customer customer = new Customer(name, email, address, null);
        return customerRepository.save(customer);
    }

    @Transactional(readOnly = true)
    public List<Customer> getAllCustomers(HttpSession session) {
        SessionHelper.requireRole(session, Role.ADMIN);
        return customerRepository.findAll();
    }

    public void deleteCustomer(HttpSession session, Long id) {
        SessionHelper.requireRole(session, Role.ADMIN);
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        customerRepository.delete(customer);
    }

    // ---- Bills ----

    public Bill generateBill(HttpSession session, Long customerId, Double unitsConsumed) {
        SessionHelper.requireRole(session, Role.ADMIN);
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));

        Bill bill = new Bill();
        bill.setCustomer(customer);
        bill.setUnitsConsumed(unitsConsumed);
        bill.setAmount(calculateAmount(unitsConsumed));
        bill.setBillDate(LocalDate.now());
        return billRepository.save(bill);
    }

    @Transactional(readOnly = true)
    public List<Bill> getAllBills(HttpSession session) {
        SessionHelper.requireRole(session, Role.ADMIN);
        return billRepository.findAll();
    }

    // ---- Billing rate logic ----
    private double calculateAmount(double units) {
        if (units <= 100) {
            return units * 3.50;
        } else if (units <= 300) {
            return (100 * 3.50) + ((units - 100) * 5.00);
        } else {
            return (100 * 3.50) + (200 * 5.00) + ((units - 300) * 7.00);
        }
    }
}
