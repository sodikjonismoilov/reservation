package com.airline.reservation.external.dto;



import java.time.LocalDate;
import java.util.List;

public record BulkStatusDTO(List<Item> items) {
    public record Item(
            String code,
            LocalDate date,
            String operatingCode,
            String operatingCarrier,
            String operatingNumber) {}
}
