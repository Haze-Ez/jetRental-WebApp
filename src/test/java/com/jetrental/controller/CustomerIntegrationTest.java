package com.jetrental.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jetrental.entity.Customer;
import com.jetrental.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Customer Controller Integration Tests")
public class CustomerIntegrationTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private CustomerRepository customerRepository;

        @Autowired
        private ObjectMapper objectMapper;

        private Customer testCustomer;

        @BeforeEach
        void setUp() {
                customerRepository.deleteAll();
                testCustomer = Customer.builder()
                                .firstName("John")
                                .lastName("Doe")
                                .email("john.doe@example.com")
                                .pilotLicense("ICEMAN")
                                .countryCode("US")
                                .build();
                testCustomer = customerRepository.save(testCustomer);
        }

        // --- API Tests ---

        @Test
        @DisplayName("API: Get All Customers")
        void testGetAllCustomers() throws Exception {
                mockMvc.perform(get("/api/customers"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(1)))
                                .andExpect(jsonPath("$[0].email", is("john.doe@example.com")));
        }

        @Test
        @DisplayName("API: Get Customer By ID")
        void testGetCustomerById() throws Exception {
                mockMvc.perform(get("/api/customers/" + testCustomer.getId()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id", is(testCustomer.getId())));
        }

        @Test
        @DisplayName("API: Create Customer")
        void testCreateCustomer() throws Exception {
                Customer newCustomer = Customer.builder().firstName("Jane").lastName("Doe").email("jane@example.com")
                                .pilotLicense("MAVERICK").countryCode("UK").build();

                mockMvc.perform(post("/api/customers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(newCustomer)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.email", is("jane@example.com")));
        }

        @Test
        @DisplayName("API: Update Customer")
        void testUpdateCustomer() throws Exception {
                testCustomer.setFirstName("Johnny");

                mockMvc.perform(put("/api/customers/" + testCustomer.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(testCustomer)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.firstName", is("Johnny")));
        }

        @Test
        @DisplayName("API: Delete Customer")
        void testDeleteCustomer() throws Exception {
                mockMvc.perform(delete("/api/customers/" + testCustomer.getId()))
                                .andExpect(status().isOk());

                assertFalse(customerRepository.existsById(testCustomer.getId()));
        }

        // --- Web Tests ---

        @Test
        @DisplayName("Web: List Customers")
        void testCustomerListPage() throws Exception {
                mockMvc.perform(get("/customers"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("customer/list"))
                                .andExpect(model().attributeExists("customers"));
        }

        @Test
        @DisplayName("Web: Show Customer Details")
        void testShowCustomerDetails() throws Exception {
                mockMvc.perform(get("/customers/" + testCustomer.getId()))
                                .andExpect(status().isOk())
                                .andExpect(view().name("customer/details"))
                                .andExpect(model().attributeExists("customer"));
        }

        @Test
        @DisplayName("Web: Show Add Form")
        void testShowAddForm() throws Exception {
                mockMvc.perform(get("/customers/add"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("customer/add"))
                                .andExpect(model().attributeExists("customer"));
        }

        @Test
        @DisplayName("Web: Add Customer Submit")
        void testAddCustomerSubmit() throws Exception {
                mockMvc.perform(post("/customers/add")
                                .param("firstName", "Tom")
                                .param("lastName", "Cruise")
                                .param("email", "tom@mav.com")
                                .param("pilotLicense", "TOPGUN")
                                .param("countryCode", "US"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/customers"));
        }

        @Test
        @DisplayName("Web: Show Edit Form")
        void testShowEditForm() throws Exception {
                mockMvc.perform(get("/customers/edit/" + testCustomer.getId()))
                                .andExpect(status().isOk())
                                .andExpect(view().name("customer/edit"))
                                .andExpect(model().attributeExists("customer"));
        }

        @Test
        @DisplayName("Web: Update Customer Submit")
        void testUpdateCustomerSubmit() throws Exception {
                mockMvc.perform(post("/customers/edit")
                                .param("id", String.valueOf(testCustomer.getId()))
                                .param("firstName", "UpdatedTom")
                                .param("lastName", "Doe")
                                .param("email", "john.doe@example.com")
                                .param("pilotLicense", "ICEMAN")
                                .param("countryCode", "US"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/customers"));
        }

        @Test
        @DisplayName("Web: Delete Customer")
        void testWebDeleteCustomer() throws Exception {
                mockMvc.perform(get("/customers/delete/" + testCustomer.getId()))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/customers"));
                assertFalse(customerRepository.existsById(testCustomer.getId()));
        }
}
