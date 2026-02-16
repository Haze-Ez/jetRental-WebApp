package com.jetrental.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jetrental.entity.Customer;
import com.jetrental.entity.Jet;
import com.jetrental.entity.RentalEvent;
import com.jetrental.repository.CustomerRepository;
import com.jetrental.repository.JetRepository;
import com.jetrental.repository.RentalEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Date;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Rental Controller Integration Tests")
public class RentalIntegrationTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private RentalEventRepository rentalEventRepository;
        @Autowired
        private JetRepository jetRepository;
        @Autowired
        private CustomerRepository customerRepository;

        @Autowired
        private ObjectMapper objectMapper;

        private Jet jet;
        private Customer customer;
        private RentalEvent rentalEvent;

        @BeforeEach
        void setUp() {
                rentalEventRepository.deleteAll();
                jetRepository.deleteAll();
                customerRepository.deleteAll();

                jet = Jet.builder().brand("TestJet").model("ModelT").seats(10).pricePerDay(1000.0).tailNumber("T001")
                                .available(true).build();
                customer = Customer.builder().firstName("Test").lastName("User").email("test@user.com")
                                .pilotLicense("L1").countryCode("US").build();

                jet = jetRepository.save(jet);
                customer = customerRepository.save(customer);

                rentalEvent = RentalEvent.builder()
                                .jetRented(jet)
                                .customerRenting(customer)
                                .rentalDate(Date.valueOf("2026-05-01"))
                                .isClosed(false)
                                .build();
        }

        // --- API Tests ---

        @Test
        @DisplayName("API: Rent Jet")
        void testRentJetApi() throws Exception {
                RentalEvent newEvent = RentalEvent.builder()
                                .jetRented(jet)
                                .customerRenting(customer)
                                .rentalDate(Date.valueOf("2026-06-01"))
                                .build();

                mockMvc.perform(post("/api/rentals")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(newEvent)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").exists());

                // Verify Jet is now unavailable
                Jet updatedJet = jetRepository.findById(jet.getId()).orElseThrow();
                assertFalse(updatedJet.isAvailable());
        }

        @Test
        @DisplayName("API: Get Rental By ID")
        void testGetRentalById() throws Exception {
                // Must make jet unavailable first normally, but we force save
                jet.setAvailable(false);
                jetRepository.save(jet);
                rentalEvent = rentalEventRepository.save(rentalEvent);

                mockMvc.perform(get("/api/rentals/" + rentalEvent.getId()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id", is(rentalEvent.getId())));
        }

        @Test
        @DisplayName("API: Return Jet")
        void testReturnJetApi() throws Exception {
                jet.setAvailable(false);
                jetRepository.save(jet);
                rentalEvent = rentalEventRepository.save(rentalEvent);

                mockMvc.perform(put("/api/rentals/return/" + rentalEvent.getId()))
                                .andExpect(status().isOk());

                Jet updatedJet = jetRepository.findById(jet.getId()).orElseThrow();
                assertTrue(updatedJet.isAvailable());
        }

        @Test
        @DisplayName("API: Delete Rental")
        void testDeleteRentalApi() throws Exception {
                jet.setAvailable(false);
                jetRepository.save(jet);
                rentalEvent = rentalEventRepository.save(rentalEvent);

                mockMvc.perform(delete("/api/rentals/" + rentalEvent.getId()))
                                .andExpect(status().isOk());

                assertFalse(rentalEventRepository.existsById(rentalEvent.getId()));
        }

        // --- Web Tests ---

        @Test
        @DisplayName("Web: List Rentals")
        void testListRentalsPage() throws Exception {
                jet.setAvailable(false);
                jetRepository.save(jet);
                rentalEventRepository.save(rentalEvent);

                mockMvc.perform(get("/rentals"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("rental/list"))
                                .andExpect(model().attributeExists("rentalEvents"));
        }

        @Test
        @DisplayName("Web: Show Add Rental Form")
        void testShowAddRentalForm() throws Exception {
                mockMvc.perform(get("/rentals/add"))
                                .andExpect(status().isOk())
                                .andExpect(view().name("rental/add"))
                                .andExpect(model().attributeExists("rentalEvent"))
                                .andExpect(model().attributeExists("jets")) // Should show available jets
                                .andExpect(model().attributeExists("customers"));
        }

        @Test
        @DisplayName("Web: Rent Jet Submit")
        void testRentJetWeb() throws Exception {
                mockMvc.perform(post("/rentals/add")
                                .param("customerRenting.id", String.valueOf(customer.getId()))
                                .param("jetRented.id", String.valueOf(jet.getId()))
                                .param("rentalDate", "2026-05-01"))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/rentals"));

                long count = rentalEventRepository.count();
                assertTrue(count > 0);
        }

        @Test
        @DisplayName("Web: Return Jet Feature")
        void testReturnJetWeb() throws Exception {
                jet.setAvailable(false);
                jetRepository.save(jet);
                rentalEvent = rentalEventRepository.save(rentalEvent);

                // Perform Return
                mockMvc.perform(get("/rentals/return/" + rentalEvent.getId()))
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrl("/rentals"));

                // Verify
                RentalEvent closedEvent = rentalEventRepository.findById(rentalEvent.getId()).orElseThrow();
                assertTrue(closedEvent.isClosed());
                assertTrue(closedEvent.getJetRented().isAvailable());
        }
}
