// src/main/java/com/airlines/reservation/external/ExternalApiExceptionHandler.java

package com.airline.reservation.external;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

@RestControllerAdvice
public class ExternalApiExceptionHandler {

    @ExceptionHandler(HttpClientErrorException.NotFound.class)
    @ResponseStatus(HttpStatus.NO_CONTENT) // or HttpStatus.NOT_FOUND if you prefer
    public void handleNotFound() {}

    @ExceptionHandler(HttpClientErrorException.class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    public String handleDownstream(HttpClientErrorException e) {
        return e.getStatusCode() + " " + e.getResponseBodyAsString();
    }
}
