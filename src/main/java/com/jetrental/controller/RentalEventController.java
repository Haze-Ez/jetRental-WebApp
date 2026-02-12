package com.jetrental.controller;

import com.jetrental.entity.RentalEvent;
import com.jetrental.service.RentalEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/rentals")
@RequiredArgsConstructor
public class RentalEventController {

    private final RentalEventService rentalService;

    @GetMapping
    public List<RentalEvent> getAllRentals() {
        return rentalService.getAllEvents();
    }

    @GetMapping("/{id}")
    public RentalEvent getRentalById(@PathVariable int id) {
        return rentalService.getEventById(id);
    }

    @PostMapping
    public RentalEvent createRental(@RequestBody RentalEvent rentalEvent) {
        // Logic handles associating Jet/Customer IDs and calculating cost
        rentalService.rentJet(rentalEvent);
        return rentalEvent;
    }

    @PutMapping("/return/{id}")
    public void returnJet(@PathVariable int id) {
        rentalService.returnJet(id);
    }

    @DeleteMapping("/{id}")
    public void deleteRental(@PathVariable int id) {
        rentalService.deleteEvent(id);
    }
}