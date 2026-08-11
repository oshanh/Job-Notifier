package org.oshanh.jobnotifier.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;

import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AIService {

    private final ChatClient chatClient;
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper; // Provided by Spring Boot by default

    public String getAiMessage(String message) {
        // We instruct the AI to return ONLY raw JSON so the ObjectMapper doesn't fail
        String systemPrompt = "You must respond ONLY with a raw JSON object containing 'subject' and 'body' keys. Do not include markdown blocks like ```json.";

        return chatClient.prompt()
                .system(systemPrompt)
                .user(message)
                .call()
                .content();
    }

    public String sendEmailWithAiMessage(String email, String promptMessage) {
        try {
            // 1. Get the JSON response from the AI
            String aiJsonResponse = getAiMessage(promptMessage);
            System.out.println(aiJsonResponse);

            // 2. Parse the JSON string into our Java Record
            AiEmailData emailData = objectMapper.readValue(aiJsonResponse, AiEmailData.class);

            // 3. Pass the separated subject and body to your NotificationService
            boolean r=notificationService.sendAiResponseGmail(email, emailData.subject(), emailData.body());
            if(r){
                return "AI response sent successfully to"+email;
            }

        } catch (Exception e) {
            log.error(e.getMessage());
            return "AI response sent failed"+email;
        }
        return "AI response sent successfully to"+email;
    }

    // A simple record to map the JSON keys to Java fields
    public record AiEmailData(String subject, String body) {}


}