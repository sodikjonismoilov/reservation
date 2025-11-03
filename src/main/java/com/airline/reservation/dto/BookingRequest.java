package com.airline.reservation.dto;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class BookingRequest {

    @NotNull
    private Long flightId;

    //either pass passengerId or include passenger details
    private Long passengerId;

    @Size(max = 100)
    private String passengerFirstName;

    @Size(max = 100)
    private String passengerLastName;

    @Size(max = 255)
    private String passengerEmail;

    @Size(max = 30)
    private String passengerPassportNumber;

    @NotNull
    @Pattern(regexp = "^[0-9]{1,2}[A-F]$|^[0-9]{1,2}" +
            "[A-Z]$|^[0-9]{1,3}$|^[A-Z][0-9]{1,2}$|^[0-9]" +
            "{1,2}[A-Z]{1,2}$|^[A-Z0-9]{1,5}$")
    private String seatNumber;


    public BookingRequest() {}

    public Long getFlightId() { return flightId; }
    public void setFlightId(Long flightId ) { this.flightId = flightId; }

    public Long getPassengerId() { return passengerId; }
    public void setPassengerId(Long passengerId) { this.passengerId = passengerId; }

    public String getPassengerFirstName() { return passengerFirstName;}
    public void setPassengerFirstName(String passengerFirstName) { this.passengerFirstName = passengerFirstName; }

    public String getPassengerLastName() { return passengerLastName; }
    public void setPassengerLastName(String passengerLastName){ this.passengerLastName = passengerLastName; }

    public String getPassengerEmail() { return passengerEmail; }
    public void setPassengerEmail(String passengerEmail) { this.passengerEmail = passengerEmail; }

    public String getPassengerPassportNumber() { return passengerPassportNumber; }
    public void setPassengerPassportNumber(String passengerPassportNumber) { this.passengerPassportNumber = passengerPassportNumber; }

    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }
}
