package com.jetrental.controller;

import com.jetrental.entity.RentalEvent;
import com.jetrental.service.RentalEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.jetrental.service.CustomerService;
import com.jetrental.service.JetService;
// ... existing code ...
import org.springframework.web.bind.WebDataBinder;
import org.springframework.beans.propertyeditors.CustomDateEditor;

import java.text.SimpleDateFormat;
import java.sql.Date;

@Controller
@RequestMapping("/rentals")
@RequiredArgsConstructor
public class RentalEventWebController {
    private final RentalEventService rentalService;
    private final CustomerService customerService;
    private final JetService jetService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        // Allow empty date strings to be bound as null (supports yyyy-MM-dd from <input type="date">)
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(sdf, true));
    }

    @GetMapping
    public String listRentals(Model model) {
        model.addAttribute("rentalEvents", rentalService.getAllEvents());
        return "rental/list";
    }

    @GetMapping("/details/{id}")
    public String rentalDetails(@PathVariable int id, Model model) {
        try {
            model.addAttribute("event", rentalService.getEventById(id));
            return "rental/details";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "rental/error";
        }
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("rentalEvent", new RentalEvent());
        model.addAttribute("customers", customerService.getAllCustomers());
        model.addAttribute("jets", jetService.getAllJets());
        return "rental/add";
    }

    @PostMapping("/add")
    public String addRental(@ModelAttribute RentalEvent event) {
        try {
            // Resolve nested ids to full entities for reliable persistence
            if (event.getCustomerRenting() != null && event.getCustomerRenting().getId() != 0) {
                var customer = customerService.getCustomerById(event.getCustomerRenting().getId());
                event.setCustomerRenting(customer);
            }
            if (event.getJetRented() != null && event.getJetRented().getId() != 0) {
                var jet = jetService.getJetById(event.getJetRented().getId());
                event.setJetRented(jet);
            }
            rentalService.rentJet(event);
            return "redirect:/rentals";
        } catch (Exception e) {
            return "redirect:/rentals?error=" + e.getMessage();
        }
    }

    @GetMapping("/return/{id}")
    public String returnJet(@PathVariable int id) {
        rentalService.returnJet(id);
        return "redirect:/rentals";
    }
}