
package com.airline.reservation.controller;


import com.airline.reservation.dto.FlightCreateRequest;
import com.airline.reservation.dto.FlightResponse;
import com.airline.reservation.service.FlightService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/flights")
public class FlightController {

    private final FlightService service;
    public FlightController(FlightService service) {
        this.service = service;
    }

    //POST /flights - > create a new flight
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FlightResponse create(@RequestBody FlightCreateRequest req) { return service.create(req); }

    //GET
    @GetMapping("/{id}")
    public FlightResponse get(@PathVariable Long id) { return service.getById(id); }

    //GET /flights?origin=JFK&destination=LAX&date=2025-11-10  -> search flights
    @GetMapping(params = { "!origin", "!destination", "!date" })
    public Page<FlightResponse> list(Pageable pageable) {
        return service.list(pageable);
    }




    @GetMapping(path = "/search", params = { "origin", "destination", "date" })
    public List<FlightResponse> search(
            @RequestParam String origin,
            @RequestParam String destination,
            @RequestParam String date
    ) {
        return service.search(origin, destination, LocalDate.parse(date));
    }

}
