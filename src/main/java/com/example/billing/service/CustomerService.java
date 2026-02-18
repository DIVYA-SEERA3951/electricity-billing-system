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

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private BillRepository billRepository;

    public Customer getProfile(HttpSession session) {
        Long userId = SessionHelper.requireRole(session, Role.CUSTOMER);
        return customerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer profile not found for your account."));
    }

    public List<Bill> getMyBills(HttpSession session) {
        Long userId = SessionHelper.requireRole(session, Role.CUSTOMER);
        Customer customer = customerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer profile not found for your account."));
        return billRepository.findByCustomerId(customer.getId());
    }
}
