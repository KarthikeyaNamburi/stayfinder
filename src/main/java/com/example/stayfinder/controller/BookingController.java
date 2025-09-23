package com.example.stayfinder.controller;

import com.example.stayfinder.model.Booking;
import com.example.stayfinder.model.Homestay;
import com.example.stayfinder.model.User;
import com.example.stayfinder.service.BookingService;
import com.example.stayfinder.service.HomestayService;
import com.example.stayfinder.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@Controller
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private HomestayService homestayService;

    @Autowired
    private UserService userService;

    @GetMapping("/book/{homestayId}")
    public String showBookingForm(@PathVariable Long homestayId, Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        Homestay homestay = homestayService.findById(homestayId);
        if (homestay == null) {
            return "redirect:/search";
        }
        Booking booking = new Booking();
        booking.setHomestay(homestay);
        String email = authentication.getName();
        User user = userService.findByEmail(email);
        booking.setUser(user);
        model.addAttribute("booking", booking);
        model.addAttribute("homestay", homestay);
        return "book";
    }

    @PostMapping("/book")
    public String createBooking(@Valid @ModelAttribute Booking booking, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            Homestay homestay = homestayService.findById(booking.getHomestay().getId());
            model.addAttribute("homestay", homestay);
            return "book";
        }
        try {
            bookingService.createBooking(booking);
            return "redirect:/search?success=booked";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            Homestay homestay = homestayService.findById(booking.getHomestay().getId());
            model.addAttribute("homestay", homestay);
            return "book";
        }
    }
}