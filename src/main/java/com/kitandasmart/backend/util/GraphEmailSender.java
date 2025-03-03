package com.kitandasmart.backend.util;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;


public class GraphEmailSender {
    private final String clientId;
    private final String clientSecret;
    private final String tenantId;
    private final String fromEmail;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public GraphEmailSender(String clientId, String clientSecret, String tenantId, String fromEmail) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.tenantId = tenantId;
        this.fromEmail = fromEmail;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    private String getAccessToken() throws IOException, InterruptedException {
        String tokenEndpoint = "https://login.microsoftonline.com/" + tenantId + "/oauth2/v2.0/token";

        String requestBody = "client_id=" + clientId +
                "&scope=https://graph.microsoft.com/.default" +
                "&client_secret=" + clientSecret +
                "&grant_type=client_credentials";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(tokenEndpoint))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Failed to get access token: " + response.body());
        }

        ObjectNode node = objectMapper.readValue(response.body(), ObjectNode.class);
        return node.get("access_token").asText();
    }

    public void sendEmail(String toEmail, String subject, String bodyContent) throws IOException, InterruptedException {
        String accessToken = getAccessToken();
        String endpoint = "https://graph.microsoft.com/v1.0/users/" + fromEmail + "/sendMail";

        // Create JSON payload
        ObjectNode rootNode = objectMapper.createObjectNode();
        ObjectNode messageNode = objectMapper.createObjectNode();

        // Subject
        messageNode.put("subject", subject);

        // Body
        ObjectNode bodyNode = objectMapper.createObjectNode();
        bodyNode.put("contentType", "Text");
        bodyNode.put("content", bodyContent);
        messageNode.set("body", bodyNode);

        // Recipients
        ArrayNode toRecipientsNode = objectMapper.createArrayNode();
        ObjectNode recipientNode = objectMapper.createObjectNode();
        ObjectNode emailAddressNode = objectMapper.createObjectNode();
        emailAddressNode.put("address", toEmail);
        recipientNode.set("emailAddress", emailAddressNode);
        toRecipientsNode.add(recipientNode);
        messageNode.set("toRecipients", toRecipientsNode);

        rootNode.set("message", messageNode);
        rootNode.put("saveToSentItems", true);

        String jsonPayload = objectMapper.writeValueAsString(rootNode);

        // Send request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header("Authorization", "Bearer " + accessToken)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            System.out.println("Email sent successfully using Microsoft Graph API!");
        } else {
            throw new IOException("Failed to send email: " + response.statusCode() + " " + response.body());
        }
    }

    // Utility method for sending login tokens
    public void sendLoginToken(String userEmail, String magicToken) throws IOException, InterruptedException {
        String subject = "Kitandasmart login access";
        String body = "Hello, use this link you access your account! http://localhost:4200/magictoken?token=" + magicToken;
        sendEmail(userEmail, subject, body);
    }
}

