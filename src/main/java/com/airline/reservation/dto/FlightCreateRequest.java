package com.airline.reservation.dto;

public class FlightCreateRequest {
    private String flightNumber;
    private String originCode;
    private String destinationCode;
    private String departureTime;
    private String arrivalTime;
    private Integer totalSeats;

    public FlightCreateRequest() {}

    //getter and setters
    public String getFlightNumber() { return  flightNumber; }
    public void setFlightNumber(String flightNumber) { this.flightNumber = flightNumber; }
    public String getOriginCode() { return  originCode; }
    public void setOriginCode(String originCode) { this.originCode = originCode; }
    public String getDestinationCode() { return  destinationCode; }
    public void setDestinationCode(String destinationCode) { this.destinationCode = destinationCode; }
    public String getDepartureTime() { return  departureTime; }
    public void setDepartureTime(String departureTime) { this.departureTime = departureTime; }
    public String getArrivalTime() { return  arrivalTime; }
    public void setArrivalTime(String arrivalTime) { this.arrivalTime = arrivalTime; }
    public Integer getTotalSeats() { return  totalSeats; }
    public void setTotalSeats(Integer totalSeats) { this.totalSeats = totalSeats; }
}
