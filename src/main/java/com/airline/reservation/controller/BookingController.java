package com.airline.reservation.controller;


import com.airline.reservation.dto.BookingRequest;
import com.airline.reservation.dto.BookingResponse;
import com.airline.reservation.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService service;

    public BookingController(BookingService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse create(@Valid @RequestBody BookingRequest request) {
        return service.create(request);
    }

    @GetMapping("/{id}")
    public BookingResponse get(@PathVariable Long id) {
        return service.get(id);
    }
    @GetMapping
    public List<BookingResponse> list(@RequestParam(required = false) Long flightId,
                                      @RequestParam(required = false) Long passengerId) {
        return service.list(flightId, passengerId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(@PathVariable Long id) {
        service.cancel(id);
    }
}
