package com.airline.reservation.repository;

import com.airline.reservation.model.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PassengerRepository extends JpaRepository<Passenger, Long> {
    boolean existsByEmail(String email);
    boolean existsByPassportNumber(String passportNumber);
    Optional<Passenger> findByEmail(String email);

}
