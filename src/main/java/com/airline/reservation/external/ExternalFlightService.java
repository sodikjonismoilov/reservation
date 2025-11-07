package com.airline.reservation.external;

import com.airline.reservation.external.dto.ExternalFlightStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;

@Service
public class ExternalFlightService {
    private static final Logger log = LoggerFactory.getLogger(ExternalFlightService.class);

    private final AmadeusClient amadeus;

    public ExternalFlightService(AmadeusClient amadeus) {
        this.amadeus = amadeus;
    }

    /**
     * Example: code=BA178 date=2025-11-07
     * Returns null when upstream has no data (controller will translate to 204).
     */
    @Cacheable(value = "flightStatus", key = "#code + '_' + #date")
    public ExternalFlightStatus getStatusByIata(String code, LocalDate date) {
        // Reject past dates early. Amadeus returns 400 for past scheduledDepartureDate.
        if (date.isBefore(LocalDate.now(ZoneOffset.UTC))) {
            throw new IllegalArgumentException("scheduledDepartureDate must be today or future");
        }

        String carrier = code.replaceAll("[^A-Za-z]", "").toUpperCase();
        String number  = code.replaceAll("[^0-9]", "");

        if (carrier.isEmpty() || number.isEmpty()) {
            log.error("Invalid flight code format: {}", code);
            throw new IllegalArgumentException("Invalid flight code. Expect letters+digits, e.g. BA178");
        }

        String path = String.format(
                "/v2/schedule/flights?carrierCode=%s&flightNumber=%s&scheduledDepartureDate=%s",
                carrier, number, date);

        log.info("Amadeus GET {}", path);

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> resp = amadeus.get(path).retrieve().body(Map.class);

            if (resp == null) {
                log.warn("Null response from Amadeus for {}", code);
                return null;
            }

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> data = (List<Map<String, Object>>) resp.get("data");

            if (data == null || data.isEmpty()) {
                log.info("No data for {} on {}", code, date);
                return null;
            }

            Map<String, Object> f = data.get(0);

            // flightDesignator: { carrierCode: "BA", flightNumber: 178 }
            @SuppressWarnings("unchecked")
            Map<String, Object> flightDesignator = (Map<String, Object>) f.get("flightDesignator");
            String cCode = str(flightDesignator, "carrierCode");
            String fNum  = str(flightDesignator, "flightNumber");

            // flightPoints: first is departure, last is arrival
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> points = (List<Map<String, Object>>) f.get("flightPoints");
            Map<String, Object> depPoint = points.get(0);
            Map<String, Object> arrPoint = points.get(points.size() - 1);

            String depIata = str(depPoint, "iataCode");
            String arrIata = str(arrPoint, "iataCode");

            // Terminals
            String depTerminal = nested(depPoint, "departure", "terminal", "code");
            String arrTerminal = nested(arrPoint, "arrival", "terminal", "code");

            // Timings arrays: STD/ETD for departure, STA/ETA for arrival
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> depTimings = (List<Map<String, Object>>) nestedObj(depPoint, "departure", "timings");
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> arrTimings = (List<Map<String, Object>>) nestedObj(arrPoint, "arrival", "timings");

            String depSTD = findTiming(depTimings, "STD");
            String depETD = findTiming(depTimings, "ETD");

            String arrSTA = findTiming(arrTimings, "STA");
            String arrETA = findTiming(arrTimings, "ETA");

            // Status usually absent in this endpoint; default to "Scheduled"
            String status = str(f, "status");
            if (status == null) status = "Scheduled";

            return new ExternalFlightStatus(
                    cCode != null ? cCode : carrier,
                    fNum  != null ? fNum  : number,
                    status,
                    depIata,
                    parseOffset(depSTD),
                    parseOffset(depETD),
                    depTerminal,
                    null, // gate not present in your payload sample
                    arrIata,
                    parseOffset(arrSTA),
                    parseOffset(arrETA),
                    arrTerminal,
                    null  // gate not present in your payload sample
            );

        } catch (HttpClientErrorException.NotFound nf) {
            log.info("Amadeus 404 for {} on {}", code, date);
            return null;
        }
    }

    private static String str(Map<?, ?> node, String key) {
        if (node == null) return null;
        Object v = node.get(key);
        return v == null ? null : v.toString();
    }

    @SuppressWarnings("unchecked")
    private static Object nestedObj(Map<String, Object> node, String... path) {
        Object cur = node;
        for (String p : path) {
            if (!(cur instanceof Map)) return null;
            cur = ((Map<String, Object>) cur).get(p);
            if (cur == null) return null;
        }
        return cur;
    }

    private static String nested(Map<String, Object> node, String... path) {
        Object o = nestedObj(node, path);
        return o == null ? null : o.toString();
    }

    private static String findTiming(List<Map<String, Object>> timings, String qualifier) {
        if (timings == null) return null;
        for (var t : timings) {
            String q = str(t, "qualifier");
            if (qualifier.equalsIgnoreCase(q)) return str(t, "value");
        }
        return null;
    }

    private static OffsetDateTime parseOffset(String iso) {
        if (iso == null) return null;
        try {
            return OffsetDateTime.parse(iso);
        } catch (Exception e) {
            return null;
        }
    }
}