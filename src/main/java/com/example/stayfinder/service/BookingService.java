package com.example.stayfinder.service;

import com.example.stayfinder.model.Booking;
import com.example.stayfinder.model.Homestay;
import com.example.stayfinder.model.User;
import com.example.stayfinder.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private HomestayService homestayService;

    public Booking createBooking(@Valid Booking booking) {
        // Validate dates: checkOut > checkIn
        if (booking.getCheckOutDate().isBefore(booking.getCheckInDate())) {
            throw new RuntimeException("Check-out date must be after check-in date");
        }

        // Check availability: no overlapping bookings
        Homestay homestay = homestayService.findById(booking.getHomestay().getId());
        List<LocalDate> availableDates = homestay.getAvailableDates();
        LocalDate checkIn = booking.getCheckInDate();
        LocalDate checkOut = booking.getCheckOutDate();

        // Simple check if dates are available (for MVP, assume daily; in real, check against bookings)
        for (LocalDate date = checkIn; date.isBefore(checkOut); date = date.plusDays(1)) {
            if (!availableDates.contains(date)) {
                throw new RuntimeException("Homestay not available on " + date);
            }
        }

        // Check for overlapping bookings
        List<Booking> existingBookings = bookingRepository.findByHomestayId(homestay.getId());
        for (Booking existing : existingBookings) {
            if (booking.getId() != existing.getId() && // not self
                !(checkOut.isBefore(existing.getCheckInDate()) || checkIn.isAfter(existing.getCheckOutDate()))) {
                throw new RuntimeException("Homestay already booked for overlapping dates");
            }
        }

        booking.setStatus(Booking.Status.PENDING);
        return bookingRepository.save(booking);
    }

    /**
     * Create a demo booking without running availability checks or payment.
     * Used by the demo flow to quickly populate the My Bookings page.
     */
    public Booking createDemoBooking(User user, Homestay homestay) {
        java.time.LocalDate checkIn;
        java.time.LocalDate checkOut;
        if (homestay.getAvailableDates() != null && !homestay.getAvailableDates().isEmpty()) {
            checkIn = homestay.getAvailableDates().get(0);
            checkOut = homestay.getAvailableDates().size() > 1
                    ? homestay.getAvailableDates().get(1)
                    : checkIn.plusDays(1);
        } else {
            checkIn = java.time.LocalDate.now();
            checkOut = checkIn.plusDays(1);
        }

        Booking booking = new Booking(user, homestay, checkIn, checkOut, Booking.Status.CONFIRMED);
        // Persist directly for demo purposes (bypass availability/payment)
        return bookingRepository.save(booking);
    }

    public List<Booking> getBookingsByUser(User user) {
        return bookingRepository.findByUserId(user.getId());
    }
}