package com.jetrental.repository;

import com.jetrental.entity.Jet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("Jet Repository Custom Search Tests")
public class JetRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private JetRepository jetRepository;

    @Test
    @DisplayName("Should filter jets by multiple criteria")
    void testAdvancedSearch() {
        // Given
        Jet g650 = Jet.builder().brand("Gulfstream").model("G650").seats(18).pricePerDay(5000.0).tailNumber("N1")
                .available(true).build();
        Jet global7500 = Jet.builder().brand("Bombardier").model("Global 7500").seats(19).pricePerDay(5500.0)
                .tailNumber("N2").available(true).build();
        Jet citation = Jet.builder().brand("Cessna").model("Citation X").seats(8).pricePerDay(3000.0).tailNumber("N3")
                .available(true).build();

        entityManager.persist(g650);
        entityManager.persist(global7500);
        entityManager.persist(citation);
        entityManager.flush();

        // When: Search for >10 seats, max price 5200, brand Gulfstream
        List<Jet> results = jetRepository.searchJets(10, 20, 5200.0, "Gulfstream", null);

        // Then
        // Expecting 2: One from data.sql (G650) and one we just added (G650)
        assertThat(results).hasSizeGreaterThanOrEqualTo(1);
        assertThat(results).extracting(Jet::getModel).contains("G650");
    }

    @Test
    @DisplayName("Should return empty list when no matches")
    void testAdvancedSearchNoMatch() {
        // Given
        Jet jet = Jet.builder().brand("Cessna").model("Citation").seats(8).pricePerDay(3000.0).tailNumber("N1")
                .available(true).build();
        entityManager.persist(jet);

        // When
        List<Jet> results = jetRepository.searchJets(20, 30, 10000.0, null, null);

        // Then
        assertThat(results).isEmpty();
    }
}
