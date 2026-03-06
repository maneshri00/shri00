package com.productivity_mangement.productivity.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.productivity_mangement.productivity.config.GoogleOAuthConfig;
import com.productivity_mangement.productivity.helper.SessionUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
//import com.google.api.services.oauth2.Oauth2;
//import com.google.api.services.oauth2.model.Userinfo;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
@Service
public class GoogleOAuthService {

    @Value("${google.client.id}")
    private String clientId;

    @Value("${google.client.secret}")
    private String clientSecret;

    @Value("${google.redirect.uri}")
    private String redirectUri;

    private Credential credential;
    private final SessionUser sessionUser;

    public GoogleOAuthService(SessionUser sessionUser) {
        this.sessionUser = sessionUser;
    }

    public String buildAuthUrl() throws IOException {

        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        new NetHttpTransport(),
                        GsonFactory.getDefaultInstance(),
                        clientId,
                        clientSecret,
                        GoogleOAuthConfig.SCOPES
                ).setAccessType("offline").build();

        return flow.newAuthorizationUrl()
                .setRedirectUri(redirectUri)
                .build();
    }

    public String exchangeCode(String code) throws IOException,GeneralSecurityException  {

        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        new NetHttpTransport(),
                        GsonFactory.getDefaultInstance(),
                        clientId,
                        clientSecret,
                        GoogleOAuthConfig.SCOPES
                ).build();

        TokenResponse tokenResponse = flow
                .newTokenRequest(code)
                .setRedirectUri(redirectUri)
                .execute();
        Credential credential = flow.createAndStoreCredential(tokenResponse, null);
        this.credential = credential;
        // ID token contains user info
        String idToken = (String) tokenResponse.get("id_token");
        System.out.println("Credential set: " + credential);
        String email = extractEmailFromIdToken(idToken);
        sessionUser.setEmail(email);
        return email;

    }



    public String extractEmailFromIdToken(String idTokenString)
            throws IOException, GeneralSecurityException {

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier
                .Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance())
                .setAudience(List.of(clientId))
                .build();

        GoogleIdToken idToken = verifier.verify(idTokenString);

        if (idToken == null) {
            throw new IllegalStateException("Invalid ID token");
        }

        return idToken.getPayload().getEmail();
    }



   public Credential getCredential() {
        return credential;
    }
}

