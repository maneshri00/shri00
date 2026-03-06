package com.productivity_mangement.productivity.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.productivity_mangement.productivity.DTO.IntegrationProvider;
import com.productivity_mangement.productivity.entity.IntegrationAccount;
import com.productivity_mangement.productivity.repository.IntegrationAccountRepository;
import com.productivity_mangement.productivity.util.TokenCipher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Service
public class SlackOAuthService {

    @Value("${slack.client.id:}")
    private String clientId;

    @Value("${slack.client.secret:}")
    private String clientSecret;

    @Value("${slack.redirect.uri:}")
    private String redirectUri;

    private final IntegrationAccountRepository integrationAccountRepository;
    private final TokenCipher tokenCipher;
    private final HttpClient httpClient;

    public SlackOAuthService(IntegrationAccountRepository integrationAccountRepository,
                             TokenCipher tokenCipher) {

        this.integrationAccountRepository = integrationAccountRepository;
        this.tokenCipher = tokenCipher;
        this.httpClient = HttpClient.newHttpClient();
    }

    // STEP 1: Generate Slack Authorization URL
    public String buildAuthUrl() {

        String scope = URLEncoder.encode(
                "channels:history,channels:read,groups:history,im:history,mpim:history",
                StandardCharsets.UTF_8
        );

        String redirect = URLEncoder.encode(redirectUri, StandardCharsets.UTF_8);

        return "https://slack.com/oauth/v2/authorize"
                + "?client_id=" + clientId
                + "&scope=" + scope
                + "&redirect_uri=" + redirect;
    }

    // STEP 2: Exchange Slack code for access token
    public void exchangeCode(String code, String userEmail) throws Exception {

        if (clientId == null || clientSecret == null ||
                clientId.isBlank() || clientSecret.isBlank()) {

            throw new IllegalStateException("Slack client credentials not configured");
        }

        String body =
                "code=" + URLEncoder.encode(code, StandardCharsets.UTF_8) +
                        "&client_id=" + URLEncoder.encode(clientId, StandardCharsets.UTF_8) +
                        "&client_secret=" + URLEncoder.encode(clientSecret, StandardCharsets.UTF_8) +
                        "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://slack.com/api/oauth.v2.access"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response =
                httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 400) {
            throw new IllegalStateException("Slack OAuth HTTP error: " + response.statusCode());
        }

        JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();

        if (!json.has("ok") || !json.get("ok").getAsBoolean()) {
            throw new IllegalStateException("Slack OAuth failed: " + response.body());
        }

        // FIX 1: access_token is NOT an object
        String accessToken = json.get("access_token").getAsString();

        // Optional workspace info
        String teamName = json.has("team")
                ? json.getAsJsonObject("team").get("name").getAsString()
                : null;

        // Check existing integration
        Optional<IntegrationAccount> existingOpt =
                integrationAccountRepository.findByUserEmailAndProvider(
                        userEmail,
                        IntegrationProvider.SLACK
                );

        IntegrationAccount account = existingOpt.orElseGet(IntegrationAccount::new);

        account.setUserEmail(userEmail);
        account.setProvider(IntegrationProvider.SLACK);
        account.setAccessToken(tokenCipher.encrypt(accessToken));
        account.setAccountLabel(teamName);

        integrationAccountRepository.save(account);
    }
}