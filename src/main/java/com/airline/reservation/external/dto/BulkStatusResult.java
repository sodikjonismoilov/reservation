package com.airline.reservation.external.dto;


import java.util.List;

public record BulkStatusResult(List<Entry> results) {
    public record Entry(String code, String date, ExternalFlightStatus status, String error) { }
}
