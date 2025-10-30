package com.airline.reservation.dto;


public class FlightResponse {
    private Long id;
    private String flightNumber;
    private String originCode;
    private String originName;
    private String destinationCode;
    private String destinationName;
    private String departureTime; // ISO
    private String arrivalTime;   // ISO
    private Integer totalSeats;
    private Integer availableSeats;

    public FlightResponse() {}

    public FlightResponse(Long id, String flightNumber,
                          String originCode, String originName,
                          String destinationCode, String destinationName,
                          String departureTime, String arrivalTime,
                          Integer totalSeats, Integer availableSeats) {
        this.id = id;
        this.flightNumber = flightNumber;
        this.originCode = originCode;
        this.originName = originName;
        this.destinationCode = destinationCode;
        this.destinationName = destinationName;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.totalSeats = totalSeats;
        this.availableSeats = availableSeats;
    }

    public Long getId() { return  id; }
    public void setId(Long id) { this.id = id; }
    public String getFlightNumber() { return  flightNumber; }
    public void setFlightNumber(String flightNumber) { this.flightNumber = flightNumber; }
    public String getOriginCode() { return  originCode; }
    public void setOriginCode(String originCode) { this.originCode = originCode; }
    public String getOriginName() { return  originName; }
    public void setOriginName(String originName) { this.originName = originName; }
    public String getDestinationCode() { return  destinationCode; }
    public void setDestinationCode(String destinationCode) { this.destinationCode = destinationCode; }
    public String getDestinationName() { return  destinationName; }
    public void setDestinationName(String destinationName) { this.destinationName = destinationName; }
    public String getDepartureTime() { return  departureTime; }
    public void setDepartureTime(String departureTime) { this.departureTime = departureTime; }
    public String getArrivalTime() { return  arrivalTime; }
    public void setArrivalTime(String arrivalTime) { this.arrivalTime = arrivalTime; }
    public Integer getTotalSeats() { return  totalSeats; }
    public void setTotalSeats(Integer totalSeats) { this.totalSeats = totalSeats; }
    public Integer getAvailableSeats() { return  availableSeats; }
    public void setAvailableSeats(Integer availableSeats) { this.availableSeats = availableSeats; }
}
