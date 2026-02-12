package com.jetrental;

import com.jetrental.entity.Jet;
import com.jetrental.repository.JetRepository;
import com.jetrental.service.JetService;
import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class JetTest {

    // ==========================================
    // 1. Service Layer Tests (Unit Logic)
    // ==========================================
    @Nested
    @DisplayName("Jet Service Logic")
    @ExtendWith(MockitoExtension.class)
    class JetServiceTests {

        @Mock
        private JetRepository jetRepository;

        @InjectMocks
        private JetService jetService;

        private Jet jet;

        @BeforeEach
        void setUp() {
            jet = Jet.builder().id(1).brand("Gulfstream").model("G650").pricePerDay(5000.0).seats(18).available(true).build();
        }

        @Test
        void testAddOrUpdateJet() {
            when(jetRepository.save(any(Jet.class))).thenReturn(jet);
            Jet saved = jetService.addOrUpdateJet(jet);
            assertNotNull(saved);
            assertEquals("Gulfstream", saved.getBrand());
        }

        @Test
        void testGetJetById_Found() {
            when(jetRepository.findById(1)).thenReturn(Optional.of(jet));
            Jet found = jetService.getJetById(1);
            assertEquals(1, found.getId());
        }

        @Test
        void testGetJetById_NotFound() {
            when(jetRepository.findById(99)).thenReturn(Optional.empty());
            assertThrows(PersistenceException.class, () -> jetService.getJetById(99));
        }

        @Test
        void testAdvancedSearch() {
            when(jetRepository.searchJets(anyInt(), anyInt(), anyDouble(), anyString(), anyString()))
                    .thenReturn(Arrays.asList(jet));
            var results = jetService.advancedSearch(0, 20, 10000, "Gulfstream", "");
            assertFalse(results.isEmpty());
        }
    }

// ... existing code ...
}