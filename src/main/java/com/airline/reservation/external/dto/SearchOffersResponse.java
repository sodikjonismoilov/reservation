package com.airline.reservation.external.dto;


import java.util.List;

public record SearchOffersResponse(
        List<SearchOfferDTO> data,
        Meta meta
) {
    public record Meta(Integer count, String self,
                       String next, String prev) {}
}
