package com.example.stayfinder.controller;

import com.example.stayfinder.model.Homestay;
import com.example.stayfinder.service.HomestayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@Controller
public class HomestayController {

    @Autowired
    private HomestayService homestayService;

    @GetMapping("/")
    public String home() {
        return "redirect:/search";
    }

    @GetMapping("/search")
    public String showSearchForm(org.springframework.ui.Model model) {
        // Load Andhra Pradesh districts from classpath JSON and expose to the template as 'districts'
        try (java.io.InputStream is = getClass().getResourceAsStream("/data/andhra_districts.json")) {
            if (is != null) {
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                java.util.List<String> districts = mapper.readValue(is, new com.fasterxml.jackson.core.type.TypeReference<java.util.List<String>>() {});
                model.addAttribute("districts", districts);
            }
        } catch (Exception e) {
            System.err.println("Could not load districts: " + e.getMessage());
        }
        return "search";
    }

    @PostMapping("/search")
    public String searchHomestays(@RequestParam String location,
                                  @RequestParam LocalDate checkIn,
                                  @RequestParam LocalDate checkOut,
                                  Model model) {
        List<Homestay> homestays = homestayService.findAvailableByLocationAndDates(location, checkIn, checkOut);

        // If no results, provide a small sample homestay so the UI shows default data
        if (homestays == null || homestays.isEmpty()) {
            com.example.stayfinder.model.User sampleHost = new com.example.stayfinder.model.User("sample@example.com", "", "Sample Host", com.example.stayfinder.model.User.Role.HOST);
            Homestay sample = new Homestay(
                    sampleHost,
                    (location == null || location.isEmpty()) ? "Sample Location" : location + " - Sample",
                    "A cozy sample homestay to help you explore the site. This is default sample data shown when no real listings match the search.",
                    1200.0,
                    java.util.Arrays.asList(checkIn, checkIn.plusDays(1))
            );
            homestays = java.util.Arrays.asList(sample);
        }

        model.addAttribute("homestays", homestays);
        model.addAttribute("location", location);
        model.addAttribute("checkIn", checkIn);
        model.addAttribute("checkOut", checkOut);
        return "search-results";
    }

    @GetMapping("/homestays/{id}")
    public String viewHomestay(@PathVariable Long id, Model model) {
        Homestay homestay = homestayService.findById(id);
        if (homestay == null) {
            return "redirect:/search";
        }
        model.addAttribute("homestay", homestay);
        return "homestay-details";
    }
}