package com.airline.reservation.external.dto;

import java.time.OffsetDateTime;

public record ExternalFlightStatus(

        String carrierCode,
        String flightNumber,
        String status,                   //scheduled, delayed, departed, arrived, cancelled ....
        String departureIata,
        OffsetDateTime departureScheduled,
        OffsetDateTime departureEstimated,
        String departureTerminal,
        String departureGate,
        String arrivalIata,
        OffsetDateTime arrivalScheduled,
        OffsetDateTime arrivalEstimated,
        String arrivalTerminal,
        String arrivalGate
) {
}
