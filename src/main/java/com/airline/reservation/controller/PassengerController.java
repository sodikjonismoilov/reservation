package com.airline.reservation.controller;


import com.airline.reservation.dto.PassengerRequest;
import com.airline.reservation.dto.PassengerResponse;
import com.airline.reservation.service.PassengerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/passengers")
public class PassengerController {

    private final PassengerService service;

    public PassengerController(PassengerService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PassengerResponse create(@Valid @RequestBody PassengerRequest request) {
        return service.create(request);
    }


    @GetMapping("/{id}")
    public PassengerResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @GetMapping
    public List<PassengerResponse> list() {
        return service.list();
    }
    @PutMapping("/{id}")
    public PassengerResponse update(@PathVariable Long id, @Valid @RequestBody PassengerRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

}
