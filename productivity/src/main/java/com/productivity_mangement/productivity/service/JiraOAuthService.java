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
public class JiraOAuthService {

    @Value("${jira.client.id:}")
    private String clientId;
    @Value("${jira.client.secret:}")
    private String clientSecret;
    @Value("${jira.redirect.uri:http://localhost:8080/auth/jira/callback}")
    private String redirectUri;

    private final IntegrationAccountRepository repo;
    private final TokenCipher cipher;
    private final HttpClient http = HttpClient.newHttpClient();

    public JiraOAuthService(IntegrationAccountRepository repo, TokenCipher cipher) {
        this.repo = repo;
        this.cipher = cipher;
    }

    public String buildAuthUrl() {
        String redirect = URLEncoder.encode(redirectUri, StandardCharsets.UTF_8);
        // Atlassian Cloud OAuth2 authorize endpoint
        return "https://auth.atlassian.com/authorize"
                + "?audience=api.atlassian.com"
                + "&client_id=" + clientId
                + "&scope=" + URLEncoder.encode("read:jira-user read:jira-work", StandardCharsets.UTF_8)
                + "&redirect_uri=" + redirect
                + "&response_type=code"
                + "&prompt=consent";
    }

    public void exchangeCode(String code, String userEmail) throws Exception {
        String body = "grant_type=authorization_code"
                + "&client_id=" + URLEncoder.encode(clientId, StandardCharsets.UTF_8)
                + "&client_secret=" + URLEncoder.encode(clientSecret, StandardCharsets.UTF_8)
                + "&code=" + URLEncoder.encode(code, StandardCharsets.UTF_8)
                + "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("https://auth.atlassian.com/oauth/token"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
        JsonObject json = JsonParser.parseString(resp.body()).getAsJsonObject();
        String access = json.has("access_token") ? json.get("access_token").getAsString() : null;
        String refresh = json.has("refresh_token") ? json.get("refresh_token").getAsString() : null;

        if (access == null || access.isBlank()) {
            throw new IllegalStateException("Jira OAuth: missing access_token");
        }

        Optional<IntegrationAccount> existing =
                repo.findByUserEmailAndProvider(userEmail, IntegrationProvider.JIRA);
        IntegrationAccount acc = existing.orElseGet(IntegrationAccount::new);
        acc.setUserEmail(userEmail);
        acc.setProvider(IntegrationProvider.JIRA);
        acc.setAccessToken(cipher.encrypt(access));
        if (refresh != null) acc.setRefreshToken(cipher.encrypt(refresh));
        repo.save(acc);
    }
}

