package com.airline.reservation.service;

import com.airline.reservation.dto.FlightCreateRequest;
import com.airline.reservation.dto.FlightResponse;
import com.airline.reservation.model.Airport;
import com.airline.reservation.model.Flight;
import com.airline.reservation.repository.AirportRepository;
import com.airline.reservation.repository.FlightRepository;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    @Transactional
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

        // 🔹 Normalize user input here
        String flightNo = req.getFlightNumber().trim().toUpperCase();
        String originCode = req.getOriginCode().trim().toUpperCase();
        String destinationCode = req.getDestinationCode().trim().toUpperCase();


        flights.findByFlightNumber(flightNo).ifPresent(f ->
        { throw new ResponseStatusException(HttpStatus.CONFLICT, "flightNumber already exists"); });

        Airport origin = airports.findByCode(originCode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown origin code"));
        Airport destination = airports.findByCode(destinationCode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown destination code"));

        LocalDateTime dep = parseIso(req.getDepartureTime(), "departureTime");
        LocalDateTime arr = parseIso(req.getArrivalTime(), "arrivalTime");

        if (!arr.isAfter(dep)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "arrivalTime must be after departureTime");
        }
        if (req.getTotalSeats() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "totalSeats must be > 0");
        }

        if (req.getPrice() == null || req.getPrice().signum() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "price must be >= 0");
        }
        if (req.getCurrency() == null || req.getCurrency().isBlank() || req.getCurrency().length() != 3) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "currency must be a 3-letter code, e.g., USD");
        }



        Flight f = new Flight(
                flightNo,
                origin,
                destination,
                dep,
                arr,
                req.getTotalSeats(),
                req.getTotalSeats(),
                req.getPrice(),
                req.getCurrency()
        );

        Flight saved = flights.save(f);
        return toResponse(saved);
    }

    public FlightResponse getById(Long id) {
        Flight f = flights.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Flight not found"));
        return toResponse(f);
    }

//    public List<FlightResponse> listAll() {
//        return flights.findAll().stream().map(this::toResponse).toList();
//    }
public Page<FlightResponse> list(Pageable pageable) {
    return flights.findAll(pageable).map(this::toResponse);
}

    public List<FlightResponse> search(String originCode, String destinationCode, LocalDate date) {
        if (originCode == null || destinationCode == null || date == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "origin, destination, date are required");
        }

        //normalize the input before hitting DB
        String from = originCode.trim().toUpperCase();
        String to = destinationCode.trim().toUpperCase();

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        return flights
                .findByOrigin_CodeAndDestination_CodeAndDepartureTimeBetween(from, to, start, end)
                .stream()
                .map(this::toResponse)
                .toList();
    }


    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private LocalDateTime parseIso(String iso, String field) {
        try {
            return LocalDateTime.parse(iso, ISO);
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
                f.getAvailableSeats(),
                f.getPrice(),
                f.getCurrency()

        );
    }
}

