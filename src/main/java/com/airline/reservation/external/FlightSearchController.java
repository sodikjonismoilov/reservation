package com.airline.reservation.external;

import com.airline.reservation.external.dto.SearchOfferDTO;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/external/search")
public class FlightSearchController {

    private final FlightOffersService service;

    public FlightSearchController(FlightOffersService service) { this.service = service; }

    @GetMapping("/offers")
    public ResponseEntity<List<SearchOfferDTO>> search(
            @RequestParam String origin,
            @RequestParam String destination,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "1") int adults,
            @RequestParam(defaultValue = "true") boolean nonStop,
            @RequestParam(defaultValue = "20") int max,
            @RequestParam(name = "currencyCode", defaultValue = "USD") String currencyCode,
            @RequestParam(name = "travelClass", defaultValue = "ECONOMY") String travelClass,
            @RequestParam(name = "includedAirlineCodes", required = false) String includedAirlineCodes,
            @RequestParam(name = "returnDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate returnDate
//            @RequestParam(name = "limit", required = false) Integer limit,
//            @RequestParam(name = "offset", required = false) Integer offset
    ) {

        if (date.isBefore(LocalDate.now(java.time.ZoneOffset.UTC))) return ResponseEntity.badRequest().build();
        if (returnDate != null && (returnDate.isBefore(date) || returnDate.isBefore(LocalDate.now(java.time.ZoneOffset.UTC))))
            return ResponseEntity.badRequest().build();

        var offers = service.search(
                origin.toUpperCase(),
                destination.toUpperCase(),
                date, adults, nonStop, max,
                currencyCode == null ? "USD" : currencyCode.toUpperCase(Locale.ROOT),
                travelClass == null ? "ECONOMY" : travelClass.toUpperCase(Locale.ROOT),
                includedAirlineCodes,
                returnDate

        );
        if (offers.isEmpty()) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(offers);
    }
}
