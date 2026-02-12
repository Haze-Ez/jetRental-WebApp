package com.jetrental.service;

import com.jetrental.entity.Customer;
import com.jetrental.repository.CustomerRepository;
import jakarta.persistence.PersistenceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;

    public Customer saveCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    public Customer getCustomerById(int id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new PersistenceException("Customer not found"));
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public void updateCustomer(int id, Customer updatedCustomer) {
        Customer existing = getCustomerById(id);
        existing.setFirstName(updatedCustomer.getFirstName());
        existing.setLastName(updatedCustomer.getLastName());
        existing.setEmail(updatedCustomer.getEmail());
        existing.setPilotLicense(updatedCustomer.getPilotLicense());
        existing.setCountryCode(updatedCustomer.getCountryCode());
        customerRepository.save(existing);
    }

    public void deleteCustomer(int id) {
        if (!customerRepository.existsById(id)) {
            throw new PersistenceException("Customer not found");
        }
        customerRepository.deleteById(id);
    }
}