package com.airline.reservation.model;


import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "flights",
        indexes = {
                @Index(name = "idx_flight_origin_dest_dep", columnList = "origin_id,destination_id,departure_time")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_flight_number", columnNames = "flight_number")
        }
)
public class Flight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "flight_number", nullable = false, unique = true, length = 10)
    private String flightNumber;                          // e.g., "UA1187"

    @ManyToOne(fetch = FetchType.LAZY, optional = false )
    @JoinColumn(name = "origin_id", nullable = false,
                  foreignKey = @ForeignKey(name = "fk_flight_origin_airport"))
    private Airport origin;


    @ManyToOne(fetch = FetchType.LAZY, optional = false )
    @JoinColumn(name = "destination_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_flight_destination_airport"))
    private Airport destination;

    @Column(name = "departure_time", nullable = false)
    private LocalDateTime departureTime;

    @Column(name = "arrival_time", nullable = false)
    private LocalDateTime arrivalTime;

    @Column(name = "total_seats", nullable = false)
    private  Integer totalSeats;

    @Column(name = "available_seats", nullable = false)
    private Integer availableSeats;


    public Flight () {}

    public Flight (String flightNumber, Airport origin, Airport destination,
                   LocalDateTime departureTime, LocalDateTime arrivalTime,
                   Integer totalSeats, Integer availableSeats) {
        this.flightNumber = flightNumber;
        this.origin = origin;
        this.destination = destination;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.totalSeats = totalSeats;
        this.availableSeats = availableSeats;
    }
    public  Long getId() { return id;}
    public void setId(Long id) { this.id = id;}
    public String getFlightNumber() { return flightNumber;}
    public void setFlightNumber(String flightNumber) { this.flightNumber = flightNumber;}
    public Airport getOrigin() { return origin;}
    public void setOrigin(Airport origin) { this.origin = origin;}
    public Airport getDestination() { return destination;}
    public void setDestination(Airport destination) { this.destination = destination;}
    public LocalDateTime getDepartureTime() { return departureTime;}
    public void setDepartureTime(LocalDateTime departureTime) { this.departureTime = departureTime;}
    public LocalDateTime getArrivalTime() { return arrivalTime;}
    public void setArrivalTime(LocalDateTime arrivalTime) { this.arrivalTime = arrivalTime;}
    public Integer getTotalSeats() { return totalSeats;}
    public void setTotalSeats(Integer totalSeats) { this.totalSeats = totalSeats;}
    public Integer getAvailableSeats() { return availableSeats;}
    public void setAvailableSeats(Integer availableSeats) { this.availableSeats = availableSeats;}

}
