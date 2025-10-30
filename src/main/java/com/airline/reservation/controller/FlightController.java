
package com.airline.reservation.controller;


import com.airline.reservation.dto.FlightCreateRequest;
import com.airline.reservation.dto.FlightResponse;
import com.airline.reservation.service.FlightService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/flights")
public class FlightController {

    private final FlightService service;

    public FlightController(FlightService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FlightResponse create(@RequestBody FlightCreateRequest req) { return service.create(req); }

    @GetMapping("/{id}")
    public FlightResponse get(@PathVariable Long id) { return service.getById(id); }

    @GetMapping
    public List<FlightResponse> search(@RequestParam String origin,
                                       @RequestParam String destination,
                                       @RequestParam String date) {
        return service.search(origin, destination, LocalDate.parse(date));
    }

}
