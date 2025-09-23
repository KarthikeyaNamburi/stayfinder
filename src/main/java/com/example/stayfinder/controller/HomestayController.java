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
    public String showSearchForm() {
        return "search";
    }

    @PostMapping("/search")
    public String searchHomestays(@RequestParam String location,
                                  @RequestParam LocalDate checkIn,
                                  @RequestParam LocalDate checkOut,
                                  Model model) {
        List<Homestay> homestays = homestayService.findAvailableByLocationAndDates(location, checkIn, checkOut);
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