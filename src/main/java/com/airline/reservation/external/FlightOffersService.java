package com.airline.reservation.external;

import com.airline.reservation.external.dto.SearchOfferDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;



@Service
public class FlightOffersService {
    private static final Logger log = LoggerFactory.getLogger(FlightOffersService.class);
    private final AmadeusClient amadeus;

    public FlightOffersService(AmadeusClient amadeus) {
        this.amadeus = amadeus;
    }

    @Cacheable(
            value = "flightOffers",
            key = "T(java.util.Objects).hash(#origin,#destination,#date,#adults,#nonStop,#max,#currencyCode," +
                    "#travelClass,#includedAirlineCodes,#returnDate)"
    )
    public List<SearchOfferDTO> search(String origin, String destination, LocalDate date,
                                       int adults, boolean nonStop, int max, String currencyCode,
                                       String travelClass, String includedAirlineCodes, LocalDate returnDate) {
        validateIata(origin); validateIata(destination);
        if (adults < 1 || adults > 9 ) throw new IllegalArgumentException("adults must be between 1 and 9");
        if (max < 1 || max > 50) max = 20;
        if (currencyCode == null || !currencyCode.matches("^[A-Z]{3}$"))
            throw new IllegalArgumentException("currencyCode must be a 3-letter ISO code, e.g., USD");
        if (travelClass == null || !travelClass.matches("^(ECONOMY|PREMIUM_ECONOMY|BUSINESS|FIRST)$"))
            throw new IllegalArgumentException("travelClass must be one of ECONOMY, PREMIUM_ECONOMY, BUSINESS, FIRST");

        if (includedAirlineCodes != null && !includedAirlineCodes.matches("^[A-Z]{2}(,[A-Z]{2})*$"))
            throw new IllegalArgumentException("includedAirlineCodes must be a comma-separated list of 2-letter IATA codes, e.g. VS,BA");
        if (returnDate != null && returnDate.isBefore(date))
            throw new IllegalArgumentException("returnDate must be after date");

        var b = org.springframework.web.util.UriComponentsBuilder.fromPath("/v2/shopping/flight-offers")
                .queryParam("originLocationCode", origin)
                .queryParam("destinationLocationCode", destination)
                .queryParam("departureDate", date.toString())
                .queryParam("adults", adults)
                .queryParam("nonStop", nonStop)
                .queryParam("max", max)
                .queryParam("currencyCode", currencyCode)
                .queryParam("travelClass", travelClass);

        if (includedAirlineCodes != null && !includedAirlineCodes.isBlank()) {
            b.queryParam("includedAirlineCodes", includedAirlineCodes);
        }
        if (returnDate != null) {
            b.queryParam("returnDate", returnDate.toString());
        }

        String path = b.build(true).toUriString();

        log.info("Amadeus GET {}", path);

        @SuppressWarnings("unchecked")
        Map<String, Object> resp = amadeus.get(path).retrieve().body(Map.class);
        if(resp == null ) return List.of();

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> data = (List<Map<String, Object>>) resp.get("data");
        if (data == null || data.isEmpty()) return List.of();

        List<SearchOfferDTO> out = new ArrayList<>(data.size());
        for(Map<String, Object> offer : data) {
            String id = str(offer, "id");

            String currency = null, total = null;
            @SuppressWarnings("unchecked") Map<String,Object> price = (Map<String,Object>) offer.get("price");
            if (price != null) { currency = str(price,"currency"); total = str(price,"total"); }


            String validatingCarrier = null;
            @SuppressWarnings("unchecked") List<String> vac = (List<String>) offer.get("validatingAirlineCodes");
            if (vac != null && !vac.isEmpty()) validatingCarrier = vac.get(0);

            @SuppressWarnings("unchecked") List<Map<String,Object>> itins = (List<Map<String,Object>>) offer.get("itineraries");
            List<SearchOfferDTO.ItineraryDTO> outIts = new ArrayList<>();
            if (itins != null) {
                for (Map<String,Object> itin : itins) {
                    String duration = str(itin,"duration");
                    @SuppressWarnings("unchecked") List<Map<String,Object>> segs = (List<Map<String,Object>>) itin.get("segments");
                    List<SearchOfferDTO.SegmentDTO> outSegs = new ArrayList<>();
                    if (segs != null) {
                        for (Map<String,Object> s : segs) {
                            String carrierCode = str(s,"carrierCode");
                            String flightNumber = str(s,"number");
                            @SuppressWarnings("unchecked") Map<String,Object> dep = (Map<String,Object>) s.get("departure");
                            @SuppressWarnings("unchecked") Map<String,Object> arr = (Map<String,Object>) s.get("arrival");
                            outSegs.add(new SearchOfferDTO.SegmentDTO(
                                    carrierCode, flightNumber,
                                    dep == null ? null : str(dep,"iataCode"),
                                    dep == null ? null : str(dep,"at"),
                                    arr == null ? null : str(arr,"iataCode"),
                                    arr == null ? null : str(arr,"at")
                            ));
                        }
                    }
                    outIts.add(new SearchOfferDTO.ItineraryDTO(duration, outSegs));
                }
            }
            out.add(new SearchOfferDTO(id, validatingCarrier, currency, total, outIts));
        }
        return out;
    }

    private static void validateIata(String x) {
        if (x == null || !x.matches("^[A-Z]{3}$"))
            throw new IllegalArgumentException("IATA code must be 3 uppercase letters");
    }
    private static String str(Map<?,?> m, String k){ if(m==null) return null; var v=m.get(k); return v==null?null:v.toString(); }
}