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

    public List<Booking> getBookingsByUser(User user) {
        return bookingRepository.findByUserId(user.getId());
    }
}