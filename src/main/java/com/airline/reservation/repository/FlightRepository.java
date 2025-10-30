package com.airline.reservation.repository;

import com.airline.reservation.model.Flight;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FlightRepository  extends JpaRepository<Flight, Long> {


    Optional<Flight> findByFlightNumber(String flightNumber);

    //handy for search later on
    List<Flight> findByOrigin_CodeAndDestination_CodeAndDepartureTimeBetween(
            String originCode,
            String destinationCode,
            LocalDateTime start,
            LocalDateTime end
    );
}
