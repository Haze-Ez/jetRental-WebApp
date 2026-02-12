package com.jetrental.controller;

import com.jetrental.entity.Jet;
import com.jetrental.service.JetService;
import jakarta.persistence.PersistenceException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/jets")
@RequiredArgsConstructor
public class JetController {

    private final JetService jetService;

    // Get all jets
    @GetMapping()
    public List<Jet> getAllJets() {
        return jetService.getAllJets();
    }

    // Get jet by ID
    @GetMapping("/{id}")
    public Jet getJetById(@PathVariable int id) {
        return jetService.getJetById(id);
    }

    // Get jets by brand
    @GetMapping("/brand/{brand}")
    public List<Jet> getJetsByBrand(@PathVariable String brand) {
        return jetService.findByBrand(brand);
    }

    // Create new jet
    @PostMapping()
    public Jet createJet(@RequestBody Jet jet) {
        return jetService.addOrUpdateJet(jet);
    }

    // Update Jet
    @PutMapping("/{id}")
    public Jet updateJet(@PathVariable int id, @RequestBody Jet jet) {
        jet.setId(id); // Ensure the ID matches the path
        return jetService.addOrUpdateJet(jet);
    }

    // Delete jet
    @DeleteMapping("/{id}")
    public void deleteJet(@PathVariable int id) {
        jetService.deleteJet(id);
    }

    // Advanced search
    @GetMapping("/search")
    public List<Jet> advancedSearch(
            @RequestParam(defaultValue = "0") int min,
            @RequestParam(defaultValue = "100") int max,
            @RequestParam(defaultValue = "1000000") double price,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String model) {
        return jetService.advancedSearch(min, max, price, brand, model);
    }
}