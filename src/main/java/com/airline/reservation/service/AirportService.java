package com.airline.reservation.service;


import com.airline.reservation.model.Airport;
import com.airline.reservation.repository.AirportRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class AirportService {

    private final AirportRepository repo;

    public AirportService(AirportRepository repo) {
        this.repo = repo;
    }
    public List<Airport> getAll() {
        return repo.findAll();
    }

    public Airport getByCode(String code) {
        return repo.findByCode(code)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Airport not found: " + code));
    }

    public Airport create(Airport a) {
        var code = a.getCode();
        if (code == null || code.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "code is required");
        }
        if (repo.findByCode(code).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Airport code already exists: " + code);
        }
        if (a.getName() == null || a.getName().isBlank()
                || a.getCity() == null || a.getCity().isBlank()
                || a.getCountry() == null || a.getCountry().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "name, city, country are required");
        }
        return repo.save(a);
    }
}
