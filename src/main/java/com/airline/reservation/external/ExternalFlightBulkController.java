package com.airline.reservation.external;

import com.airline.reservation.external.dto.BulkStatusDTO;
import com.airline.reservation.external.dto.BulkStatusResult;
import com.airline.reservation.external.dto.ExternalFlightStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/external/flights/status")
public class ExternalFlightBulkController {

    private final ExternalFlightService service;

    public ExternalFlightBulkController(ExternalFlightService service) {
        this.service = service;
    }

    @PostMapping("/bulk")
    public ResponseEntity<BulkStatusResult> bulk(@RequestBody BulkStatusDTO body) {
        if (body == null || body.items() == null || body.items().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        LocalDate todayUtc = LocalDate.now(ZoneOffset.UTC);
        List<BulkStatusResult.Entry> out = new ArrayList<>(body.items().size());

        for (var it : body.items()) {
            String code = it.code();
            LocalDate date = it.date();

            if (!hasText(code) || date == null) {
                out.add(new BulkStatusResult.Entry(String.valueOf(code), String.valueOf(date), null, "INVALID_INPUT"));
                continue;
            }
            if (date.isBefore(todayUtc)) {
                out.add(new BulkStatusResult.Entry(code, date.toString(), null, "PAST_DATE"));
                continue;
            }

            ExternalFlightStatus status = service.getStatusByIata(code, date);

            if (status == null) {
                out.add(new BulkStatusResult.Entry(code, date.toString(), null, "NOT_FOUND"));
            } else {
                out.add(new BulkStatusResult.Entry(code, date.toString(), status, null));
            }
        }

        return ResponseEntity.ok(new BulkStatusResult(out));
    }

    private static boolean hasText(String s) {
        return s != null && !s.isBlank();
    }
}