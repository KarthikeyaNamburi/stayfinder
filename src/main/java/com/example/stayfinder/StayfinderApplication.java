package com.example.stayfinder;

import com.example.stayfinder.model.Homestay;
import com.example.stayfinder.model.User;
import com.example.stayfinder.service.HomestayService;
import com.example.stayfinder.service.UserService;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class StayfinderApplication {

	public static void main(String[] args) {
		SpringApplication.run(StayfinderApplication.class, args);
	}

	@Bean
	public CommandLineRunner demo(UserService userService, HomestayService homestayService) {
		return (args) -> {
			// Create a host user
			User host = new User("host@example.com", "hostpass", "Community Host", User.Role.HOST);
			User existingHost = userService.findByEmail(host.getEmail());
			if (existingHost == null) {
				host = userService.registerUser(host);
			} else {
				host = existingHost;
			}
	
			// Create sample homestays
			List<LocalDate> dates1 = Arrays.asList(LocalDate.of(2024, 10, 1), LocalDate.of(2024, 10, 2), LocalDate.of(2024, 10, 3));
			Homestay h1 = new Homestay(host, "Visakhapatnam", "Cozy homestay near beach", 1500.0, dates1);
			homestayService.save(h1);

			List<LocalDate> dates2 = Arrays.asList(LocalDate.of(2024, 10, 5), LocalDate.of(2024, 10, 6));
			Homestay h2 = new Homestay(host, "Vijayawada", "Traditional Andhra home", 1200.0, dates2);
			homestayService.save(h2);

			List<LocalDate> dates3 = Arrays.asList(LocalDate.of(2024, 10, 10), LocalDate.of(2024, 10, 11), LocalDate.of(2024, 10, 12));
			Homestay h3 = new Homestay(host, "Guntur", "Family-friendly stay", 1000.0, dates3);
			homestayService.save(h3);

			List<LocalDate> dates4 = Arrays.asList(LocalDate.of(2024, 10, 15), LocalDate.of(2024, 10, 16));
			Homestay h4 = new Homestay(host, "Tirupati", "Near temple, spiritual retreat", 2000.0, dates4);
			homestayService.save(h4);

			List<LocalDate> dates5 = Arrays.asList(LocalDate.of(2024, 10, 20), LocalDate.of(2024, 10, 21), LocalDate.of(2024, 10, 22));
			Homestay h5 = new Homestay(host, "Kurnool", "Rustic village homestay", 800.0, dates5);
			homestayService.save(h5);
		};
	}
}
