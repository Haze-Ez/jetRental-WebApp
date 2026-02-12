package com.jetrental.service;

import com.jetrental.entity.Customer;
import com.jetrental.entity.Jet;
import com.jetrental.entity.RentalEvent;
import com.jetrental.repository.CustomerRepository;
import com.jetrental.repository.JetRepository;
import com.jetrental.repository.RentalEventRepository;
import jakarta.persistence.PersistenceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RentalEventService {
    private final RentalEventRepository rentalEventRepository;
    private final JetRepository jetRepository;
    private final CustomerRepository customerRepository;

    @Transactional
    public void rentJet(RentalEvent event) {
        Jet jet = jetRepository.findById(event.getJetRented().getId())
                .orElseThrow(() -> new PersistenceException("Jet not found"));
        Customer customer = customerRepository.findById(event.getCustomerRenting().getId())
                .orElseThrow(() -> new PersistenceException("Customer not found"));

        if (!jet.isAvailable()) {
            throw new IllegalStateException("Jet is not available");
        }

        // Calculate Cost if return date is present
        if (event.getReturnDate() != null && event.getRentalDate() != null) {
            long diffInMillies = Math.abs(event.getReturnDate().getTime() - event.getRentalDate().getTime());
            long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
            if (diff == 0) diff = 1; // Minimum 1 day
            event.setTotalCost(diff * jet.getPricePerDay());
        }

        event.setJetRented(jet);
        event.setCustomerRenting(customer);
        event.setClosed(false);

        jet.setAvailable(false);
        jetRepository.save(jet);
        rentalEventRepository.save(event);
    }

    @Transactional
    public void returnJet(int eventId) {
        RentalEvent event = rentalEventRepository.findById(eventId)
                .orElseThrow(() -> new PersistenceException("Rental Event not found"));

        if (event.isClosed()) {
            throw new IllegalStateException("Event already closed");
        }

        // Set return date to now if not set
        if (event.getReturnDate() == null) {
            event.setReturnDate(new Date(System.currentTimeMillis()));
        }

        // Compute total cost at return time (covers the case where returnDate was previously null)
        if (event.getRentalDate() != null && event.getJetRented() != null) {
            long diffInMillies = Math.abs(event.getReturnDate().getTime() - event.getRentalDate().getTime());
            long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
            if (diff == 0) diff = 1; // Minimum 1 day
            event.setTotalCost(diff * event.getJetRented().getPricePerDay());
        }

        event.setClosed(true);

        Jet jet = event.getJetRented();
        jet.setAvailable(true);
        jetRepository.save(jet);
        rentalEventRepository.save(event);
    }

    public List<RentalEvent> getAllEvents() {
        return rentalEventRepository.findAll();
    }

    public RentalEvent getEventById(int id) {
        return rentalEventRepository.findById(id).orElseThrow(() -> new PersistenceException("Not Found"));
    }

    public void deleteEvent(int id) {
        rentalEventRepository.deleteById(id);
    }
}