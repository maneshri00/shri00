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
public class GitHubOAuthService {

    @Value("${github.client.id:}")
    private String clientId;
    @Value("${github.client.secret:}")
    private String clientSecret;
    @Value("${github.redirect.uri:http://localhost:8080/auth/github/callback}")
    private String redirectUri;

    private final IntegrationAccountRepository repo;
    private final TokenCipher cipher;
    private final HttpClient http = HttpClient.newHttpClient();

    public GitHubOAuthService(IntegrationAccountRepository repo, TokenCipher cipher) {
        this.repo = repo;
        this.cipher = cipher;
    }

    public String buildAuthUrl() {
        String redirect = URLEncoder.encode(redirectUri, StandardCharsets.UTF_8);
        return "https://github.com/login/oauth/authorize"
                + "?client_id=" + clientId
                + "&redirect_uri=" + redirect
                + "&scope=" + URLEncoder.encode("repo,read:org,read:user", StandardCharsets.UTF_8);
    }

    public void exchangeCode(String code, String userEmail) throws Exception {
        String body = "client_id=" + URLEncoder.encode(clientId, StandardCharsets.UTF_8)
                + "&client_secret=" + URLEncoder.encode(clientSecret, StandardCharsets.UTF_8)
                + "&code=" + URLEncoder.encode(code, StandardCharsets.UTF_8)
                + "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("https://github.com/login/oauth/access_token"))
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
        JsonObject json = JsonParser.parseString(resp.body()).getAsJsonObject();
        String access = json.has("access_token") ? json.get("access_token").getAsString() : null;

        if (access == null || access.isBlank()) {
            throw new IllegalStateException("GitHub OAuth: missing access_token");
        }

        Optional<IntegrationAccount> existing =
                repo.findByUserEmailAndProvider(userEmail, IntegrationProvider.GITHUB);
        IntegrationAccount acc = existing.orElseGet(IntegrationAccount::new);
        acc.setUserEmail(userEmail);
        acc.setProvider(IntegrationProvider.GITHUB);
        acc.setAccessToken(cipher.encrypt(access));
        repo.save(acc);
    }
}

