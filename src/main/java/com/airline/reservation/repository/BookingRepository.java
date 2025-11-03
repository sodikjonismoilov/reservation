package com.airline.reservation.repository;


import com.airline.reservation.model.Booking;
import com.airline.reservation.model.BookingStatus;
import com.airline.reservation.model.Flight;
import com.airline.reservation.model.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    long countByFlightAndStatus(Flight flight, BookingStatus status);
    boolean existsByFlightAndSeatNumberAndStatus(Flight flight, String seatNumber, BookingStatus status);
    boolean existsByFlightAndPassengerAndStatus(Flight flight, Passenger passenger, BookingStatus status);
    List<Booking> findByFlightId(Long flightId);
    List<Booking> findByPassengerId(Long passengerId);
}