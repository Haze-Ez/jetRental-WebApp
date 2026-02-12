package com.jetrental.service;

import com.jetrental.entity.Jet;
import com.jetrental.repository.JetRepository;
import jakarta.persistence.PersistenceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JetService {
    private final JetRepository jetRepository;

    // Matches 'addOrUpdateBoat' functionality
    @Transactional
    public Jet addOrUpdateJet(Jet jet) {
        try {
            return jetRepository.save(jet);
        } catch (Exception e) {
            throw new PersistenceException("Failed to save/update jet: " + e.getMessage());
        }
    }

    public List<Jet> getAllJets() {
        return jetRepository.findAll();
    }

    public Jet getJetById(int id) {
        return jetRepository.findById(id)
                .orElseThrow(() -> new PersistenceException("Jet not found"));
    }

    public List<Jet> findByBrand(String brand) {
        return jetRepository.findByBrand(brand);
    }

    public List<Jet> advancedSearch(int min, int max, double price, String brand, String model) {
        // Handle nulls gracefully as seen in the reference Controller logic
        String searchBrand = (brand == null) ? "" : brand;
        String searchModel = (model == null) ? "" : model;
        return jetRepository.searchJets(min, max, price, searchBrand, searchModel);
    }

    public void deleteJet(int id) {
        if (!jetRepository.existsById(id)) {
            throw new PersistenceException("Jet not found");
        }
        jetRepository.deleteById(id);
    }
}