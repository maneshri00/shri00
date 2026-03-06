package com.productivity_mangement.productivity.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.MessagePartHeader;
import com.productivity_mangement.productivity.DTO.EmailDTO;
import com.productivity_mangement.productivity.DTO.Task;
//import com.poductivity_mangement.productivity.controller.GoogleAuthController;
import com.google.api.services.gmail.model.Message;
import com.productivity_mangement.productivity.service.mapper.GmailTaskMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class GmailService {

    private final GoogleOAuthService oauthService;

    public GmailService(GoogleOAuthService oauthService) {
        this.oauthService = oauthService;
    }

    public List<Task> readInboxAsTasks() throws IOException {

        Credential cred = oauthService.getCredential();
        if (cred == null) throw new IllegalStateException("Login first");

        Gmail gmail = new Gmail.Builder(
                new NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                cred
        ).setApplicationName("Tusk").build();

        List<Task> tasks = new ArrayList<>();

        ListMessagesResponse response = gmail.users()
                .messages()
                .list("me")
                .setLabelIds(List.of("INBOX"))
                .setMaxResults(10L)
                .execute();

        if (response.getMessages() == null) return tasks;

        for (Message m : response.getMessages()) {

            Message full = gmail.users()
                    .messages()
                    .get("me", m.getId())
                    .execute();

            tasks.add(GmailTaskMapper.mapToTask(full));
        }
        System.out.println("Credential in GmailService: " + cred);

        return tasks;


}
    private EmailDTO toEmailDTO(Message msg) {

        EmailDTO dto = new EmailDTO();
        dto.setId(msg.getId());
        dto.setSnippet(msg.getSnippet());
        dto.setTimestamp(msg.getInternalDate());
        dto.setLabels(msg.getLabelIds());

        String from = null;
        String subject = null;

        if (msg.getPayload() != null && msg.getPayload().getHeaders() != null) {
            for (MessagePartHeader h : msg.getPayload().getHeaders()) {
                if ("From".equalsIgnoreCase(h.getName())) {
                    from = h.getValue();
                } else if ("Subject".equalsIgnoreCase(h.getName())) {
                    subject = h.getValue();
                }
            }
        }

        dto.setFrom(from);
        dto.setSubject(subject);

        return dto;
    }
}
