package com.airline.reservation.controller;


import com.airline.reservation.model.Airport;
import com.airline.reservation.service.AirportService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/airports")
public class AirportController {

    private final AirportService service;

    public AirportController(AirportService service){
        this.service = service;
    }

    @GetMapping
    public List<Airport> list(){
       return service.getAll();
    }
    @GetMapping("/{code}")
    public Airport byCode(@PathVariable String code) {
        return service.getByCode(code);
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Airport create(@RequestBody Airport airport) {
        return service.create(airport);
    }
}
