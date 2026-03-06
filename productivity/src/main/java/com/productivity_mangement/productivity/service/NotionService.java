package com.productivity_mangement.productivity.service;

import com.productivity_mangement.productivity.DTO.NotionTaskDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class NotionService {

    @Value("${notion.token}")
    private String notionToken;

    @Value("${notion.database.id}")
    private String databaseId;

    private static final String NOTION_VERSION = "2022-06-28";

    public List<NotionTaskDTO> getTasks() {

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(notionToken);
        headers.set("Notion-Version", NOTION_VERSION);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>("{}", headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                "https://api.notion.com/v1/databases/" + databaseId + "/query",
                HttpMethod.POST,
                entity,
                Map.class
        );

        return parseTasks(response.getBody());
    }

    private List<NotionTaskDTO> parseTasks(Map<String, Object> response) {

        List<NotionTaskDTO> tasks = new ArrayList<>();

        if (response == null || !response.containsKey("results")) {
            return tasks;
        }

        List<Map<String, Object>> results =
                (List<Map<String, Object>>) response.get("results");

        for (Map<String, Object> page : results) {

            NotionTaskDTO dto = new NotionTaskDTO();
            String pageId = (String) page.get("id");

            dto.setId(pageId);
            dto.setUrl((String) page.get("url"));

            Map<String, Object> properties =
                    (Map<String, Object>) page.get("properties");


            if (properties != null && properties.containsKey("Name")) {

                Map<String, Object> titleProp =
                        (Map<String, Object>) properties.get("Name");

                List<Map<String, Object>> titleArr =
                        (List<Map<String, Object>>) titleProp.get("title");

                if (titleArr != null && !titleArr.isEmpty()) {
                    dto.setTitle(
                            (String) titleArr.get(0).get("plain_text")
                    );
                }
            }


            dto.setContent(fetchPageContent(pageId));

            tasks.add(dto);
        }

        return tasks;
    }


    private String fetchPageContent(String pageId) {

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(notionToken);
        headers.set("Notion-Version", NOTION_VERSION);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                "https://api.notion.com/v1/blocks/" + pageId + "/children",
                HttpMethod.GET,
                entity,
                Map.class
        );

        if (response.getBody() == null || !response.getBody().containsKey("results")) {
            return "";
        }

        List<Map<String, Object>> results =
                (List<Map<String, Object>>) response.getBody().get("results");

        StringBuilder content = new StringBuilder();

        for (Map<String, Object> block : results) {

            String type = (String) block.get("type");

            if ("paragraph".equals(type)) {
                Map<String, Object> paragraph =
                        (Map<String, Object>) block.get("paragraph");

                List<Map<String, Object>> richText =
                        (List<Map<String, Object>>) paragraph.get("rich_text");

                if (richText != null) {
                    for (Map<String, Object> text : richText) {
                        content.append(text.get("plain_text")).append("\n");
                    }
                }
            }
        }

        return content.toString().trim();
    }
}
