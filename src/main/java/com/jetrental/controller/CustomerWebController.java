package com.jetrental.controller;

import com.jetrental.entity.Customer;
import com.jetrental.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerWebController {

    private final CustomerService customerService;

    @GetMapping
    public String listCustomers(Model model) {
        model.addAttribute("customers", customerService.getAllCustomers());
        return "customer/list";
    }

    @GetMapping("/{id}")
    public String showCustomerDetails(@PathVariable int id, Model model) {
        try {
            model.addAttribute("customer", customerService.getCustomerById(id));
            return "customer/details";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "customer/error";
        }
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("customer", new Customer());
        return "customer/add";
    }

    @PostMapping("/add")
    public String addCustomer(@ModelAttribute Customer customer) {
        customerService.saveCustomer(customer);
        return "redirect:/customers";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable int id, Model model) {
        try {
            model.addAttribute("customer", customerService.getCustomerById(id));
            return "customer/edit";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "customer/error";
        }
    }

    @PostMapping("/edit")
    public String updateCustomer(@ModelAttribute Customer customer) {
        customerService.updateCustomer(customer.getId(), customer);
        return "redirect:/customers";
    }

    @GetMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable int id) {
        customerService.deleteCustomer(id);
        return "redirect:/customers";
    }
}