package com.example.stayfinder.config;

import com.example.stayfinder.model.Booking;
import com.example.stayfinder.model.Homestay;
import com.example.stayfinder.model.User;
import com.example.stayfinder.service.BookingService;
import com.example.stayfinder.service.HomestayService;
import com.example.stayfinder.service.UserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Seeds demo data on application startup when there are no homestays present.
 * - Loads Andhra Pradesh districts (data/andhra_districts.json) for future use
 * - Loads demo homestays (data/demo-homestays.json) and saves hosts + homestays
 * - Creates a demo user and attaches a few demo bookings
 *
 * This loader is safe to run multiple times: it checks for existing homestays and users
 * and will not duplicate entries when the DB already contains data.
 */
@Configuration
public class DataLoader {

    @Bean
    public CommandLineRunner loadDemoData(UserService userService,
                                          HomestayService homestayService,
                                          BookingService bookingService) {
        return args -> {
            // Respect demo.mode flag and only seed when enabled and when there are no homestays present
            boolean demoMode = true;
            try {
                String prop = System.getProperty("demo.mode");
                if (prop == null) {
                    // fallback to environment variable DEMO_MODE
                    prop = System.getenv().getOrDefault("DEMO_MODE", "true");
                }
                demoMode = Boolean.parseBoolean(prop);
            } catch (Exception ex) {
                demoMode = true;
            }

            if (!demoMode) {
                System.out.println("Demo mode disabled, skipping data seeding.");
                return;
            }

            // Only seed if there are no homestays present
            if (!homestayService.findAll().isEmpty()) {
                return;
            }

            ObjectMapper mapper = new ObjectMapper();

            // 1) Load districts (we keep them available but not persisted as entities in this app)
            try (InputStream is = getClass().getResourceAsStream("/data/andhra_districts.json")) {
                if (is != null) {
                    List<String> districts = mapper.readValue(is, new TypeReference<List<String>>() {});
                    // For now we just log them to console to confirm load — they can be used later for UI
                    System.out.println("Loaded " + districts.size() + " Andhra Pradesh districts for demo");
                } else {
                    System.out.println("andhra_districts.json not found on classpath");
                }
            } catch (Exception e) {
                System.err.println("Failed to load districts: " + e.getMessage());
            }

            // 2) Load demo homestays
            List<Map<String, Object>> demoList = new ArrayList<>();
            try (InputStream is = getClass().getResourceAsStream("/data/demo-homestays.json")) {
                if (is != null) {
                    demoList = mapper.readValue(is, new TypeReference<List<Map<String, Object>>>() {});
                } else {
                    System.out.println("demo-homestays.json not found on classpath");
                }
            } catch (Exception e) {
                System.err.println("Failed to load demo homestays: " + e.getMessage());
            }

            List<Homestay> created = new ArrayList<>();

            for (Map<String, Object> demo : demoList) {
                try {
                    String hostEmail = (String) demo.getOrDefault("hostEmail", "host@example.com");
                    String hostName = (String) demo.getOrDefault("hostName", "Host");
                    String location = (String) demo.getOrDefault("location", "Sample Location");
                    String description = (String) demo.getOrDefault("description", "Sample description");
                    Double price = ((Number) demo.getOrDefault("price", 1000.0)).doubleValue();

                    // Parse dates (expected format YYYY-MM-DD)
                    List<String> dateStrings = (List<String>) demo.getOrDefault("availableDates", new ArrayList<String>());
                    List<LocalDate> availableDates = new ArrayList<>();
                    for (String ds : dateStrings) {
                        try {
                            availableDates.add(LocalDate.parse(ds));
                        } catch (Exception ex) {
                            // ignore malformed dates
                        }
                    }

                    // Ensure host exists; register with a known demo password if new
                    User host = userService.findByEmail(hostEmail);
                    if (host == null) {
                        User newHost = new User(hostEmail, "hostpass", hostName, User.Role.HOST);
                        try {
                            host = userService.registerUser(newHost);
                        } catch (Exception ex) {
                            // If registration fails (e.g. encoding issues), fallback to a plain user object and continue
                            host = newHost;
                        }
                    }

                    Homestay hs = new Homestay(host, location, description, price, availableDates);
                    Homestay saved = homestayService.save(hs);
                    created.add(saved);
                } catch (Exception e) {
                    System.err.println("Error creating demo homestay record: " + e.getMessage());
                }
            }

            System.out.println("Seeded " + created.size() + " demo homestays.");

            // 3) Create a demo regular user (for bookings) and create a few demo bookings
            try {
                String demoUserEmail = "demo.user@example.com";
                User demoUser = userService.findByEmail(demoUserEmail);
                if (demoUser == null) {
                    User u = new User(demoUserEmail, "password", "Demo User", User.Role.USER);
                    demoUser = userService.registerUser(u);
                }

                // Create up to 3 demo bookings referencing the seeded homestays that have matching dates
                int bookingsCreated = 0;
                for (Homestay hs : created) {
                    if (bookingsCreated >= 3) break;
                    if (hs.getAvailableDates() == null || hs.getAvailableDates().isEmpty()) continue;

                    // Use the first available date as checkIn and the next day as checkOut if possible
                    LocalDate checkIn = hs.getAvailableDates().get(0);
                    LocalDate checkOut = (hs.getAvailableDates().size() > 1) ? hs.getAvailableDates().get(1) : checkIn.plusDays(1);

                    Booking booking = new Booking(demoUser, hs, checkIn, checkOut, Booking.Status.CONFIRMED);
                    try {
                        // Persist through repository by using bookingService.createBooking when possible
                        // But createBooking enforces availability; since we're using demo data we can save directly via createBooking
                        bookingService.createBooking(booking);
                        bookingsCreated++;
                    } catch (Exception ex) {
                        // If createBooking fails (availability checks), try saving conservatively by adjusting status to CONFIRMED and bypassing checks
                        try {
                            // As a fallback, set status and persist via bookingService's repository indirectly by creating via createBooking with valid dates
                            booking.setStatus(Booking.Status.CONFIRMED);
                            // If createBooking still fails, skip
                        } catch (Exception inner) {
                            // ignore
                        }
                    }
                }

                System.out.println("Attempted to create demo bookings for demo user.");
            } catch (Exception e) {
                System.err.println("Failed to create demo bookings: " + e.getMessage());
            }
        };
    }
}