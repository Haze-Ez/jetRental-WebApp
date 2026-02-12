package com.jetrental.controller;

import com.jetrental.entity.Jet;
import com.jetrental.service.JetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/jets")
@RequiredArgsConstructor
public class JetWebController {

    private final JetService jetService;

    @GetMapping
    public String listJets(Model model) {
        model.addAttribute("jets", jetService.getAllJets());
        return "jet/list";
    }

    @GetMapping("/{id}")
    public String showJetDetails(@PathVariable int id, Model model) {
        try {
            model.addAttribute("jet", jetService.getJetById(id));
            return "jet/details";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "jet/error";
        }
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("jet", new Jet());
        return "jet/add";
    }

    @PostMapping("/add")
    public String addJet(@ModelAttribute Jet jet) {
        jetService.addOrUpdateJet(jet);
        return "redirect:/jets";
    }

    @GetMapping("/filter")
    public String showFilterForm(Model model) {
        model.addAttribute("jet", new Jet());
        model.addAttribute("jets", jetService.getAllJets());
        return "jet/filter";
    }

    @PostMapping("/filter")
    public String filter(@RequestParam(required = false) Integer min,
                         @RequestParam(required = false) Integer max,
                         @RequestParam(required = false) Double price,
                         @RequestParam(required = false) String brand,
                         @RequestParam(required = false) String modelVal,
                         Model model) {
        List<Jet> jets = jetService.advancedSearch(
                min != null ? min : 0,
                max != null ? max : Integer.MAX_VALUE,
                price != null ? price : Double.MAX_VALUE,
                brand,
                modelVal
        );
        model.addAttribute("jets", jets);
        return "jet/filteredJets";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable int id, Model model) {
        try {
            model.addAttribute("jet", jetService.getJetById(id));
            return "jet/edit";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "jet/error";
        }
    }

    @PostMapping("/edit")
    public String updateJet(@ModelAttribute Jet jet) {
        jetService.addOrUpdateJet(jet);
        return "redirect:/jets";
    }

    @GetMapping("/delete/{id}")
    public String deleteJet(@PathVariable int id) {
        jetService.deleteJet(id);
        return "redirect:/jets";
    }
}