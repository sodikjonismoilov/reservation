package com.airline.reservation.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;

@Entity
@Table(
        name = "bookings",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_booking_flight_seat", columnNames = { "flight_id", "seat_number"})
        },
        indexes = {
                @Index(name = "idx_booking_flight", columnList = "flight_id"),
                @Index(name = "idx_booking_passenger", columnList = "passenger_id"),
                @Index(name = "idx_booking_status", columnList = "status")
        }
)
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "flight_id", nullable = false, foreignKey = @ForeignKey(name = "fk_booking_flight"))
    private Flight flight;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "passenger_id", nullable = false, foreignKey = @ForeignKey(name = "fk_booking_passenger"))
    private Passenger passenger;

    @NotBlank
    @Size(max = 5)
    @Column(name = "seat_number", nullable = false, length = 5)
    private String seatNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    private BookingStatus status = BookingStatus.CONFIRMED;

    @Column(name = "booking_date", nullable = false)
    private OffsetDateTime bookingDate = OffsetDateTime.now();

    @Version
    private Long version;

    public Booking() {}

    public Booking(Flight flight, Passenger passenger, String seatNumber){
        this.flight = flight;
        this.passenger = passenger;
        this.seatNumber = seatNumber;
        this.status = BookingStatus.CONFIRMED;
    }
    public Long getId() { return id; }
//    public void setId(Long id) { this.id = id; }
    public Flight getFlight() { return flight; }
    public void setFlight(Flight flight) { this.flight = flight; }
    public Passenger getPassenger() { return passenger; }
    public void setPassenger(Passenger passenger) { this.passenger = passenger; }
    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }
    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }
    public OffsetDateTime getBookingDate() { return bookingDate; }
    public void setBookingDate(OffsetDateTime bookingDate) { this.bookingDate = bookingDate; }
    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
}



