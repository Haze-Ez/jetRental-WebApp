package com.jetrental;

import com.jetrental.entity.Customer;
import com.jetrental.repository.CustomerRepository;
import com.jetrental.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CustomerTest {

    @Nested
    @DisplayName("Customer Service Logic")
    @ExtendWith(MockitoExtension.class)
    class CustomerServiceTests {

        @Mock private CustomerRepository customerRepository;
        @InjectMocks private CustomerService customerService;

        private Customer customer;

        @BeforeEach
        void setUp() {
            customer = Customer.builder().id(1).firstName("Tom").lastName("Cruise").email("tom@topgun.com").build();
        }

        @Test
        void testSaveCustomer() {
            when(customerRepository.save(any(Customer.class))).thenReturn(customer);
            Customer saved = customerService.saveCustomer(customer);
            assertEquals("Tom", saved.getFirstName());
        }

        @Test
        void testDeleteCustomer() {
            when(customerRepository.existsById(1)).thenReturn(true);
            assertDoesNotThrow(() -> customerService.deleteCustomer(1));
            verify(customerRepository).deleteById(1);
        }
    }

}