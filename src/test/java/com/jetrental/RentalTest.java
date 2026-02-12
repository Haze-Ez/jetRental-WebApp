package com.jetrental;

import com.jetrental.entity.Customer;
import com.jetrental.entity.Jet;
import com.jetrental.entity.RentalEvent;
import com.jetrental.repository.CustomerRepository;
import com.jetrental.repository.JetRepository;
import com.jetrental.repository.RentalEventRepository;
import com.jetrental.service.RentalEventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RentalTest {

    @Nested
    @DisplayName("Rental Service Logic")
    @ExtendWith(MockitoExtension.class)
    class RentalServiceTests {

        @Mock
        private RentalEventRepository rentalEventRepository;
        @Mock
        private JetRepository jetRepository;
        @Mock
        private CustomerRepository customerRepository;
        @InjectMocks
        private RentalEventService rentalService;

        private Jet jet;
        private Customer customer;
        private RentalEvent event;

        @BeforeEach
        void setUp() {
            jet = Jet.builder().id(1).pricePerDay(1000).available(true).build();
            customer = Customer.builder().id(1).build();
            event = RentalEvent.builder()
                    .jetRented(jet)
                    .customerRenting(customer)
                    .rentalDate(Date.valueOf("2026-01-01"))
                    .returnDate(Date.valueOf("2026-01-03"))
                    .build();
        }

        @Test
        void testRentJet_Success() {
            when(jetRepository.findById(1)).thenReturn(Optional.of(jet));
            when(customerRepository.findById(1)).thenReturn(Optional.of(customer));

            rentalService.rentJet(event);

            assertFalse(jet.isAvailable());
            assertEquals(2000.0, event.getTotalCost());
            verify(rentalEventRepository).save(event);
        }

        @Test
        void testRentJet_Unavailable() {
            jet.setAvailable(false);
            when(jetRepository.findById(1)).thenReturn(Optional.of(jet));
            when(customerRepository.findById(1)).thenReturn(Optional.of(customer));

            assertThrows(IllegalStateException.class, () -> rentalService.rentJet(event));
        }

        @Test
        void testReturnJet() {
            event.setClosed(false);
            when(rentalEventRepository.findById(1)).thenReturn(Optional.of(event));

            rentalService.returnJet(1);

            assertTrue(event.isClosed());
            assertTrue(jet.isAvailable());
        }
    }
}
