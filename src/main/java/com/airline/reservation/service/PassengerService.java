package com.airline.reservation.service;

import com.airline.reservation.dto.PassengerRequest;
import com.airline.reservation.dto.PassengerResponse;
import com.airline.reservation.model.Passenger;
import com.airline.reservation.repository.PassengerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PassengerService {

    private final PassengerRepository repo;

    public PassengerService(PassengerRepository repo) {
        this.repo = repo;
    }

    public PassengerResponse create(PassengerRequest req) {
        if (repo.existsByEmail(req.getEmail()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
        if (repo.existsByPassportNumber(req.getPassportNumber()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Passport number already in use");

        Passenger p = new Passenger(
                req.getFirstName().trim(),
                req.getLastName().trim(),
                req.getEmail().toLowerCase().trim(),
                req.getPassportNumber().trim()
        );
        Passenger saved = repo.save(p);
        return toResponse(saved);
    }

    public PassengerResponse get(Long id){
        Passenger p = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Passenger not found"));
        return toResponse(p);
    }
    public List<PassengerResponse> list() {
        return repo.findAll().stream().map(this::toResponse).toList();
    }
    public PassengerResponse update(Long id, PassengerRequest req) {
        Passenger p = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Passenger not found"));

        if (!p.getEmail().equalsIgnoreCase(req.getEmail()) && repo.existsByEmail(req.getEmail()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");

        if (!p.getPassportNumber().equals(req.getPassportNumber()) && repo.existsByPassportNumber(req.getPassportNumber()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Passport number already in use");

        p.setFirstName(req.getFirstName().trim());
        p.setLastName(req.getLastName().trim());
        p.setEmail(req.getEmail().toLowerCase().trim());
        p.setPassportNumber(req.getPassportNumber().trim());

        return toResponse(repo.save(p));
    }

    public void delete(Long id) {
        if (!repo.existsById(id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Passenger not found");
        repo.deleteById(id);
    }

    private PassengerResponse toResponse(Passenger p) {
        return new PassengerResponse(
                p.getId(),
                p.getFirstName(),
                p.getLastName(),
                p.getEmail(),
                p.getPassportNumber(),
                p.getCreatedAt()
        );
    }


}
