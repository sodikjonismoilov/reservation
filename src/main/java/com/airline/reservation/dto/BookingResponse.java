package com.airline.reservation.dto;


import com.airline.reservation.model.BookingStatus;

import java.time.OffsetDateTime;

public class BookingResponse {

    private Long id;
    private Long flightId;
    private Long passengerId;
    private String seatNumber;
    private BookingStatus status;
    private OffsetDateTime bookingDate;

    public BookingResponse() {}

    public BookingResponse(Long id, Long flightId, Long passengerId,
                           String seatNumber, BookingStatus status, OffsetDateTime bookingDate) {
        this.id = id;
        this.flightId = flightId;
        this.passengerId = passengerId;
        this.seatNumber = seatNumber;
        this.status = status;
        this.bookingDate = bookingDate;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getFlightId() { return flightId; }
    public void setFlightId(Long flightId) { this.flightId = flightId; }
    public Long getPassengerId() { return passengerId; }
    public void setPassengerId(Long passengerId) { this.passengerId = passengerId; }
    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }
    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }
    public OffsetDateTime getBookingDate() { return bookingDate; }
    public void setBookingDate(OffsetDateTime bookingDate) { this.bookingDate = bookingDate; }
}
