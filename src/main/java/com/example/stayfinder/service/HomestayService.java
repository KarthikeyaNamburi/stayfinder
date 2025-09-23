package com.example.stayfinder.service;

import com.example.stayfinder.model.Homestay;
import com.example.stayfinder.repository.HomestayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class HomestayService {

    @Autowired
    private HomestayRepository homestayRepository;

    public List<Homestay> findAll() {
        return homestayRepository.findAll();
    }

    public List<Homestay> findByLocation(String location) {
        return homestayRepository.findByLocationContainingIgnoreCase(location);
    }

    public List<Homestay> findAvailableByLocationAndDates(String location, LocalDate checkIn, LocalDate checkOut) {
        return homestayRepository.findAvailableByLocationAndDates(location, checkIn, checkOut);
    }

    public Homestay save(Homestay homestay) {
        return homestayRepository.save(homestay);
    }

    public Homestay findById(Long id) {
        return homestayRepository.findById(id).orElse(null);
    }
}