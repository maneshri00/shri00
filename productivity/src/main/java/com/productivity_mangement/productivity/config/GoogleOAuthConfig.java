package com.productivity_mangement.productivity.config;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class GoogleOAuthConfig {

    public static final String APPLICATION_NAME = "Tusk";

    public static final List<String> SCOPES = List.of(
            "openid",
            "email",
            "profile",
            "https://www.googleapis.com/auth/gmail.readonly",
            "https://www.googleapis.com/auth/calendar.readonly"
    );


    @Bean
    public JsonFactory jsonFactory() {
        return GsonFactory.getDefaultInstance();
    }

    @Bean
    public NetHttpTransport httpTransport() {
        return new NetHttpTransport();
    }
}


