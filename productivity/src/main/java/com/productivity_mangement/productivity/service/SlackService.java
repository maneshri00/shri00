package com.productivity_mangement.productivity.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.productivity_mangement.productivity.DTO.Task;
import com.productivity_mangement.productivity.DTO.TaskScope;
import com.productivity_mangement.productivity.DTO.TaskSource;
import com.productivity_mangement.productivity.DTO.TaskStatus;
import com.productivity_mangement.productivity.DTO.IntegrationProvider;
import com.productivity_mangement.productivity.entity.IntegrationAccount;
import com.productivity_mangement.productivity.repository.IntegrationAccountRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.productivity_mangement.productivity.util.TokenCipher;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SlackService {

    private final IntegrationAccountRepository integrationAccountRepository;
    private final TokenCipher tokenCipher;
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Value("${slack.api.base:https://slack.com/api}")
    private String slackApiBase;

    public SlackService(IntegrationAccountRepository integrationAccountRepository,
                        TokenCipher tokenCipher) {
        this.integrationAccountRepository = integrationAccountRepository;
        this.tokenCipher = tokenCipher;
    }

    public List<Task> fetchTasks(String userEmail) {
        Optional<IntegrationAccount> accountOpt =
                integrationAccountRepository.findByUserEmailAndProvider(
                        userEmail,
                        IntegrationProvider.SLACK
                );

        if (accountOpt.isEmpty()) {
            return List.of();
        }

        String token = tokenCipher.decrypt(accountOpt.get().getAccessToken());
        if (token == null || token.isBlank()) {
            return List.of();
        }

        try {
            // Fetch last N messages from conversations the bot/user is part of
            List<String> channelIds = fetchChannelIds(token);
            List<Task> tasks = new ArrayList<>();

            for (String channelId : channelIds) {
                tasks.addAll(fetchChannelMessagesAsTasks(token, channelId));
            }

            return tasks;
        } catch (Exception e) {
            return List.of();
        }
    }

    private List<String> fetchChannelIds(String token) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(slackApiBase + "/conversations.list?types=public_channel,private_channel,im,mpim&limit=5"))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        HttpResponse<String> response =
                httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 400) {
            return List.of();
        }

        JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
        if (!json.has("channels")) {
            return List.of();
        }

        List<String> ids = new ArrayList<>();
        JsonArray channels = json.getAsJsonArray("channels");
        for (JsonElement el : channels) {
            JsonObject ch = el.getAsJsonObject();
            if (ch.has("id")) {
                ids.add(ch.get("id").getAsString());
            }
        }
        return ids;
    }

    private List<Task> fetchChannelMessagesAsTasks(String token, String channelId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(slackApiBase + "/conversations.history?channel=" + channelId + "&limit=10"))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        HttpResponse<String> response =
                httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 400) {
            return List.of();
        }

        JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
        if (!json.has("messages")) {
            return List.of();
        }

        List<Task> tasks = new ArrayList<>();
        JsonArray messages = json.getAsJsonArray("messages");

        for (JsonElement el : messages) {
            JsonObject msg = el.getAsJsonObject();
            if (!msg.has("text")) continue;

            Task task = new Task();
            task.setId(null);
            task.setSource(TaskSource.SLACK);
            task.setScope(TaskScope.PROFESSIONAL);
            task.setStatus(TaskStatus.TODO);

            String ts = msg.has("ts") ? msg.get("ts").getAsString() : null;
            if (ts != null) {
                long epochSeconds = (long) Double.parseDouble(ts);
                LocalDateTime createdAt =
                        LocalDateTime.ofInstant(
                                Instant.ofEpochSecond(epochSeconds),
                                ZoneId.systemDefault()
                        );
                task.setCreatedAt(createdAt);
            }

            task.setTitle(truncate(msg.get("text").getAsString(), 80));
            task.setContent(msg.get("text").getAsString());

            tasks.add(task);
        }
        return tasks;
    }

    private String truncate(String s, int maxLen) {
        if (s == null) return null;
        if (s.length() <= maxLen) return s;
        return s.substring(0, maxLen);
    }
}

