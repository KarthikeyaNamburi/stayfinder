package com.example.stayfinder.repository;

import com.example.stayfinder.model.Homestay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HomestayRepository extends JpaRepository<Homestay, Long> {
    List<Homestay> findByLocationContainingIgnoreCase(String location);

    @Query("SELECT h FROM Homestay h WHERE h.location LIKE %:location% AND EXISTS (SELECT 1 FROM h.availableDates ad WHERE ad >= :checkIn AND ad < :checkOut)")
    List<Homestay> findAvailableByLocationAndDates(@Param("location") String location, @Param("checkIn") java.time.LocalDate checkIn, @Param("checkOut") java.time.LocalDate checkOut);
}