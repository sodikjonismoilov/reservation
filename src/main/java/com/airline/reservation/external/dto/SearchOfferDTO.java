package com.airline.reservation.external.dto;

import java.util.List;

public record SearchOfferDTO(
        String id,
        String validatingCarrier,
        String currency,
        String total,
        List<ItineraryDTO> itineraries
) {
    public record ItineraryDTO(String duration, List<SegmentDTO> segments) {}
    public record SegmentDTO(
            String carrierCode, String flightNumber,
            String departureIata, String departureAt,
            String arrivalIata, String arrivalAt
    ) {}
}