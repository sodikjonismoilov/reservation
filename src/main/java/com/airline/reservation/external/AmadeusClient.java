package com.airline.reservation.external;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.util.Map;

@Component
public class AmadeusClient {
    private final RestClient http;
    private final String clientId;
    private final String clientSecret;
    private final String baseUrl;

    private volatile String accessToken;
    private volatile Instant tokenExpiry = Instant.EPOCH;

    public AmadeusClient(
            @Value("${amadeus.baseUrl}") String baseUrl,
            @Value("${amadeus.clientId}") String clientId,
            @Value("${amadeus.clientSecret}") String clientSecret
    ) {
        this.baseUrl = baseUrl;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.http = RestClient.builder().baseUrl(baseUrl).build();
    }

    private String token() {
        if (accessToken != null && Instant.now().isBefore(tokenExpiry.minusSeconds(30))) {
            return accessToken;
        }

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "client_credentials");
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);

        @SuppressWarnings("unchecked")
        Map<String, Object> resp = http.post()
                .uri("/v1/security/oauth2/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .body(Map.class);

        this.accessToken = (String) resp.get("access_token");
        Number expiresIn = (Number) resp.getOrDefault("expires_in", 1700);
        this.tokenExpiry = Instant.now().plusSeconds(expiresIn.longValue());
        return accessToken;
    }

    public RestClient.RequestHeadersSpec<?> get(String path) {
        return http.get().uri(path).header("Authorization", "Bearer " + token());
    }
}