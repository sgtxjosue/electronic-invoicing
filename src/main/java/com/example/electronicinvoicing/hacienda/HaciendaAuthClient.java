package com.example.electronicinvoicing.hacienda;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class HaciendaAuthClient {

    private static final Logger logger = LoggerFactory.getLogger(HaciendaAuthClient.class);

    private final String authUrl;
    private final String clientId;
    private final String username;
    private final String password;

    public HaciendaAuthClient(
            @Value("${hacienda.auth.url}") String authUrl,
            @Value("${hacienda.client.id}") String clientId,
            @Value("${hacienda.username}") String username,
            @Value("${hacienda.password}") String password) {
        this.authUrl = authUrl;
        this.clientId = clientId;
        this.username = username;
        this.password = password;
    }

    public String getAccessToken() {
        logger.info("Requesting Hacienda access token from {}", authUrl);
        // Backbone only: real HTTP call and token caching to be implemented later.
        return "stub-access-token";
    }
}

