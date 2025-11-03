package com.airline.reservation.service;


import com.airline.reservation.dto.BookingRequest;
import com.airline.reservation.dto.BookingResponse;
import com.airline.reservation.model.Booking;
import com.airline.reservation.model.BookingStatus;
import com.airline.reservation.model.Flight;
import com.airline.reservation.model.Passenger;
import com.airline.reservation.repository.BookingRepository;
import com.airline.reservation.repository.FlightRepository;
import com.airline.reservation.repository.PassengerRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookings;
    private final PassengerRepository passengers;
    private final FlightRepository flights;

    public BookingService(BookingRepository bookings, PassengerRepository passengers, FlightRepository flights) {
        this.bookings = bookings;
        this.passengers = passengers;
        this.flights = flights;
    }

     @Transactional
    public BookingResponse create(BookingRequest req){
        Flight flight = flights.findById(req.getFlightId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Flight not found"));
        Passenger passenger;
        if(req.getPassengerId() != null) {
            passenger = passengers.findById(req.getPassengerId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Passenger not found"));
        } else {
            if(req.getPassengerFirstName() == null || req.getPassengerLastName() == null ||
            req.getPassengerEmail() == null || req.getPassengerPassportNumber() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passenger details are required when passengerId is not provided");
            }
            //create passenger on the fly if not exists by email; otherwise reuse existing
            passenger = passengers.findByEmail(req.getPassengerEmail().toLowerCase().trim())
                    .orElseGet(() -> passengers.save(
                            new Passenger(
                                    req.getPassengerFirstName().trim(),
                                    req.getPassengerLastName().trim(),
                                    req.getPassengerEmail().toLowerCase().trim(),
                                    req.getPassengerPassportNumber().trim()
                            )
                    ));
        }
        long confirmedCount = bookings.countByFlightAndStatus(flight, BookingStatus.CONFIRMED);
        if (flight.getTotalSeats() != null && confirmedCount >= flight.getTotalSeats()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Flight is fully booked");
        }
         String seat = req.getSeatNumber().trim().toUpperCase();
         if (bookings.existsByFlightAndSeatNumberAndStatus(flight, seat, BookingStatus.CONFIRMED)) {
             throw new ResponseStatusException(HttpStatus.CONFLICT, "Seat already taken");
         }

         if (bookings.existsByFlightAndPassengerAndStatus(flight, passenger, BookingStatus.CONFIRMED)) {
             throw new ResponseStatusException(HttpStatus.CONFLICT, "Passenger already has a confirmed booking on this flight");
         }

         Booking booking = new Booking(flight, passenger, seat);
         Booking saved = bookings.save(booking);
         return toResponse(saved);
     }

    public BookingResponse get(Long id) {
        Booking b = bookings.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));
        return toResponse(b);
    }

    public List<BookingResponse> list(Long flightId, Long passengerId) {
        if (flightId != null) {
            return bookings.findByFlightId(flightId).stream().map(this::toResponse).toList();
        }
        if (passengerId != null) {
            return bookings.findByPassengerId(passengerId).stream().map(this::toResponse).toList();
        }
        return bookings.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional
    public void cancel(Long id) {
        Booking b = bookings.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));
        if (b.getStatus() == BookingStatus.CANCELLED) return;
        b.setStatus(BookingStatus.CANCELLED);
        bookings.save(b);
    }

    private BookingResponse toResponse(Booking b) {
        return new BookingResponse(
                b.getId(),
                b.getFlight().getId(),
                b.getPassenger().getId(),
                b.getSeatNumber(),
                b.getStatus(),
                b.getBookingDate()
        );
    }
}
