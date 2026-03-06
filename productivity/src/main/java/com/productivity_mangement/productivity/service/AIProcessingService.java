package com.productivity_mangement.productivity.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.productivity_mangement.productivity.DTO.StructuredTaskDTO;
import com.productivity_mangement.productivity.DTO.Task;
import com.productivity_mangement.productivity.DTO.EmailDTO;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AIProcessingService {

    private static final Logger log = LoggerFactory.getLogger(AIProcessingService.class);

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final MeterRegistry meterRegistry;

    @Value("${openai.api.key:}")
    private String apiKey;

    @Value("${openai.model:gpt-4.1}")
    private String model;

    public AIProcessingService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public StructuredTaskDTO extractStructured(String title,
                                               String content,
                                               String sender,
                                               String sourcePlatform) {
        if (apiKey == null || apiKey.isBlank()) {
            return null;
        }
        try {
            Task t = new Task();
            t.setTitle(title);
            t.setContent(content);
            t.setSender(sender);
            return callOpenAI(t);
        } catch (Exception e) {
            log.error("extractStructured failed", e);
            return null;
        }
    }

    public String inferProfessionFromEmails(List<EmailDTO> emails) {
        if (apiKey == null || apiKey.isBlank()) {
            return null;
        }
        try {
            StringBuilder sb = new StringBuilder();
            int limit = Math.min(20, emails.size());
            for (int i = 0; i < limit; i++) {
                EmailDTO e = emails.get(i);
                sb.append("From: ").append(safe(e.getFrom())).append("\n");
                sb.append("Subject: ").append(safe(e.getSubject())).append("\n");
                sb.append("Snippet: ").append(safe(e.getSnippet())).append("\n\n");
            }
            String userContent = sb.toString();
            String body = """
                    {
                      "model": "%s",
                      "response_format": { "type": "json_object" },
                      "messages": [
                        {
                          "role": "system",
                          "content": "You analyze email headers and snippets to infer the user's profession. Reply only as JSON: {\"profession\":\"\",\"confidence\":0-100}. If unsure, set profession to \"Unknown\"."
                        },
                        {
                          "role": "user",
                          "content": %s
                        }
                      ]
                    }
                    """.formatted(model, toJsonString(userContent));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 400) {
                return null;
            }
            String content = extractContentFromResponse(response.body());
            if (content == null || content.isBlank()) {
                return null;
            }
            try {
                JsonObject obj = JsonParser.parseString(content).getAsJsonObject();
                if (obj.has("profession")) {
                    return obj.get("profession").getAsString();
                }
            } catch (Exception ignored) {}
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }
    public void enrichTask(Task task) {
        if (apiKey == null || apiKey.isBlank()) {
            return;
        }

        try {
            Timer.Sample sample = Timer.start(meterRegistry);

            StructuredTaskDTO structured = callOpenAI(task);

            sample.stop(
                    meterRegistry.timer("openai.enrich_task.latency")
            );

            if (structured == null) {
                meterRegistry.counter("openai.enrich_task.empty_response").increment();
                return;
            }

            if (structured.getTaskTitle() != null && !structured.getTaskTitle().isBlank()) {
                task.setTitle(structured.getTaskTitle());
            }

            if (structured.getDescription() != null && !structured.getDescription().isBlank()) {
                task.setDescription(structured.getDescription());
            }

            if (structured.getDeadline() != null) {
                task.setDueDate(structured.getDeadline());
            }

            if (structured.getEstimatedTimeMinutes() != null) {
                task.setEstimatedMinutes(structured.getEstimatedTimeMinutes());
            }

            if (structured.getUrgencyScore() != null) {
                task.setUrgencyScore(structured.getUrgencyScore());
            }

            if (structured.getImportanceScore() != null) {
                task.setImportanceScore(structured.getImportanceScore());
            }

            meterRegistry.counter("openai.enrich_task.success").increment();

        } catch (Exception e) {
            meterRegistry.counter("openai.enrich_task.error").increment();
            log.error("Failed to enrich task with OpenAI", e);
        }
    }

    private StructuredTaskDTO callOpenAI(Task task) throws Exception {
        String prompt = buildPrompt(task);

        String body = """
                {
                  "model": "%s",
                  "response_format": { "type": "json_object" },
                  "messages": [
                    {
                      "role": "system",
                      "content": "You are a productivity assistant that extracts a single actionable task from a message. Respond strictly as JSON with the specified fields."
                    },
                    {
                      "role": "user",
                      "content": %s
                    }
                  ]
                }
                """.formatted(model, toJsonString(prompt));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response =
                httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 400) {
            log.warn("OpenAI API error: status={} body={}", response.statusCode(), response.body());
            return null;
        }

        String content = extractContentFromResponse(response.body());

        if (content == null || content.isBlank()) {
            return null;
        }

        return parseStructuredTask(content);
    }

    private String buildPrompt(Task task) {
        StringBuilder sb = new StringBuilder();
        sb.append("Extract a single actionable task from the following message.\n");
        sb.append("Return a JSON object with exactly these keys:\n");
        sb.append("task_title (string), description (string), deadline (ISO-8601 datetime or empty string),\n");
        sb.append("sender (string), source_platform (string), urgency_score (integer 0-100),\n");
        sb.append("importance_score (integer 0-100), suggested_priority (LOW|MEDIUM|HIGH),\n");
        sb.append("tags (array of strings), estimated_time (integer minutes).\n");
        sb.append("Message title: ").append(task.getTitle()).append("\n");
        sb.append("Message content: ").append(task.getContent() != null ? task.getContent() : "").append("\n");
        return sb.toString();
    }

    private String toJsonString(String s) {
        // Serialize a Java string as a JSON string literal using Gson
        return new com.google.gson.Gson().toJson(s);
    }

    /**
     * Parses the raw OpenAI Chat Completions HTTP response body and extracts
     * the content string from choices[0].message.content using Gson.
     */
    private String extractContentFromResponse(String responseBody) {
        try {
            JsonObject root = JsonParser.parseString(responseBody).getAsJsonObject();
            JsonArray choices = root.getAsJsonArray("choices");
            if (choices == null || choices.isEmpty()) return null;
            JsonObject message = choices.get(0).getAsJsonObject().getAsJsonObject("message");
            if (message == null) return null;
            JsonElement contentEl = message.get("content");
            if (contentEl == null || contentEl.isJsonNull()) return null;
            return contentEl.getAsString();
        } catch (Exception e) {
            log.warn("Failed to extract content from OpenAI response: {}", e.getMessage());
            return null;
        }
    }

    private StructuredTaskDTO parseStructuredTask(String json) {
        StructuredTaskDTO dto = new StructuredTaskDTO();
        try {
            JsonObject obj = JsonParser.parseString(json).getAsJsonObject();

            dto.setTaskTitle(getStringOrNull(obj, "task_title"));
            dto.setDescription(getStringOrNull(obj, "description"));
            dto.setSender(getStringOrNull(obj, "sender"));
            dto.setSourcePlatform(getStringOrNull(obj, "source_platform"));
            dto.setSuggestedPriority(getStringOrNull(obj, "suggested_priority"));

            String deadline = getStringOrNull(obj, "deadline");
            if (deadline != null && !deadline.isBlank()) {
                try {
                    dto.setDeadline(LocalDateTime.parse(deadline));
                } catch (Exception ignored) {
                    // partial date formats – skip
                }
            }

            // estimated_time may be integer or string
            if (obj.has("estimated_time")) {
                JsonElement et = obj.get("estimated_time");
                try {
                    if (et.isJsonPrimitive()) {
                        String etStr = et.getAsString().replaceAll("\\D", "");
                        if (!etStr.isBlank()) dto.setEstimatedTimeMinutes(Integer.parseInt(etStr));
                    }
                } catch (Exception ignored) {}
            }

            if (obj.has("urgency_score") && obj.get("urgency_score").isJsonPrimitive())
                dto.setUrgencyScore(obj.get("urgency_score").getAsInt());
            if (obj.has("importance_score") && obj.get("importance_score").isJsonPrimitive())
                dto.setImportanceScore(obj.get("importance_score").getAsInt());

            if (obj.has("tags") && obj.get("tags").isJsonArray()) {
                List<String> tags = new ArrayList<>();
                for (JsonElement t : obj.getAsJsonArray("tags")) {
                    tags.add(t.getAsString());
                }
                dto.setTags(tags);
            }
        } catch (Exception e) {
            log.warn("Failed to parse structured task JSON: {}", e.getMessage());
        }
        return dto;
    }

    private String getStringOrNull(JsonObject obj, String key) {
        if (!obj.has(key) || obj.get(key).isJsonNull()) return null;
        return obj.get(key).getAsString();
    }
}

