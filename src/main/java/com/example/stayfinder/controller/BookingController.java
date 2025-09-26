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

import java.util.List;

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

    /**
     * Quick demo booking endpoint: creates a confirmed booking for the demo user without payment.
     * Redirects to /bookings so the new booking appears in My Bookings.
     */
    @GetMapping("/book/demo/{homestayId}")
    public String createDemoBooking(@PathVariable Long homestayId) {
        Homestay homestay = homestayService.findById(homestayId);
        if (homestay == null) {
            return "redirect:/search";
        }

        String demoEmail = "demo.user@example.com";
        User demoUser = userService.findByEmail(demoEmail);
        if (demoUser == null) {
            User u = new User(demoEmail, "password", "Demo User", User.Role.USER);
            try {
                demoUser = userService.registerUser(u);
            } catch (Exception ex) {
                // If registration fails, fallback to a non-persisted demo user object (booking will still be saved if possible)
                demoUser = u;
            }
        }

        try {
            bookingService.createDemoBooking(demoUser, homestay);
            return "redirect:/bookings?success=demo";
        } catch (Exception e) {
            // On failure, go back to homestay with a simple error flag
            return "redirect:/homestays/" + homestayId + "?error=demo_failed";
        }
    }

    @GetMapping("/bookings")
    public String listUserBookings(Authentication authentication, @RequestParam(required = false) String success, Model model) {
        User user = null;
        boolean demoMode = false;

        if (authentication != null && authentication.isAuthenticated()) {
            user = userService.findByEmail(authentication.getName());
        }

        // If no authenticated user, fall back to demo user so the page shows demo bookings
        if (user == null) {
            user = userService.findByEmail("demo.user@example.com");
            demoMode = true;
        }

        if (user == null) {
            model.addAttribute("bookings", List.of());
            model.addAttribute("demoMode", demoMode);
            model.addAttribute("success", success);
            return "bookings";
        }

        List<Booking> bookings = bookingService.getBookingsByUser(user);
        model.addAttribute("bookings", bookings);
        model.addAttribute("demoMode", demoMode);
        model.addAttribute("success", success);
        return "bookings";
    }
}