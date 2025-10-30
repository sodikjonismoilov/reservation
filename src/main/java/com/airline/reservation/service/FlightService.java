package com.airline.reservation.service;

import com.airline.reservation.dto.FlightCreateRequest;
import com.airline.reservation.dto.FlightResponse;
import com.airline.reservation.model.Airport;
import com.airline.reservation.model.Flight;
import com.airline.reservation.repository.AirportRepository;
import com.airline.reservation.repository.FlightRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
public class FlightService {

    private final FlightRepository flights;
    private final AirportRepository airports;

    public FlightService(FlightRepository flights, AirportRepository airports) {
        this.flights = flights;
        this.airports = airports;
    }

    public FlightResponse create(FlightCreateRequest req) {
        // Basic required fields
        if (req.getFlightNumber() == null || req.getFlightNumber().isBlank()
                || req.getOriginCode() == null || req.getOriginCode().isBlank()
                || req.getDestinationCode() == null || req.getDestinationCode().isBlank()
                || req.getDepartureTime() == null || req.getDepartureTime().isBlank()
                || req.getArrivalTime() == null || req.getArrivalTime().isBlank()
                || req.getTotalSeats() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "All fields are required");
        }

        if (req.getOriginCode().equalsIgnoreCase(req.getDestinationCode())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "origin and destination must differ");
        }

        flights.findByFlightNumber(req.getFlightNumber()).ifPresent(f ->
        { throw new ResponseStatusException(HttpStatus.CONFLICT, "flightNumber already exists"); });

        Airport origin = airports.findByCode(req.getOriginCode())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown origin code"));
        Airport destination = airports.findByCode(req.getDestinationCode())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown destination code"));

        LocalDateTime dep = parseIso(req.getDepartureTime(), "departureTime");
        LocalDateTime arr = parseIso(req.getArrivalTime(), "arrivalTime");

        if (!arr.isAfter(dep)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "arrivalTime must be after departureTime");
        }
        if (req.getTotalSeats() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "totalSeats must be > 0");
        }

        Flight f = new Flight(
                req.getFlightNumber(),
                origin,
                destination,
                dep,
                arr,
                req.getTotalSeats(),
                req.getTotalSeats()
        );

        Flight saved = flights.save(f);
        return toResponse(saved);
    }

    public FlightResponse getById(Long id) {
        Flight f = flights.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Flight not found"));
        return toResponse(f);
    }

    public List<FlightResponse> search(String originCode, String destinationCode, LocalDate date) {
        if (originCode == null || destinationCode == null || date == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "origin, destination, date are required");
        }
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        return flights
                .findByOrigin_CodeAndDestination_CodeAndDepartureTimeBetween(originCode, destinationCode, start, end)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private LocalDateTime parseIso(String iso, String field) {
        try {
            return LocalDateTime.parse(iso);
        } catch (DateTimeParseException ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    field + " must be ISO-8601 like 2025-11-03T08:00"
            );
        }
    }

    private FlightResponse toResponse(Flight f) {
        return new FlightResponse(
                f.getId(),
                f.getFlightNumber(),
                f.getOrigin().getCode(),
                f.getOrigin().getName(),
                f.getDestination().getCode(),
                f.getDestination().getName(),
                f.getDepartureTime().toString(),
                f.getArrivalTime().toString(),
                f.getTotalSeats(),
                f.getAvailableSeats()
        );
    }
}

