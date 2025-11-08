package com.airline.reservation.external;

import com.airline.reservation.external.dto.ExternalFlightStatus;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.ZoneOffset;

@RestController
@RequestMapping("/external/flights")
public class ExternalFlightController {

    private final ExternalFlightService service;

    public ExternalFlightController(ExternalFlightService service) {
        this.service = service;
    }

    // GET /external/flights/status?code=BA178&date=2025-11-07
    @GetMapping("/status")
    public ResponseEntity<ExternalFlightStatus> status(
            @RequestParam String code,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        if (date.isBefore(LocalDate.now(ZoneOffset.UTC))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "scheduledDepartureDate must be today or future");
        }

        ExternalFlightStatus dto = service.getStatusByIata(code, date);
        if (dto == null) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(dto);
    }

    @GetMapping("")
    public java.util.Map<String, String> index() {
        return java.util.Map.of(
                "status", "/external/flights/status?code=BA178&date=2025-11-07",
                "search", "/external/search/offers?origin=JFK&destination=LHR&date=2025-11-08&adults=1"
        );
    }
}