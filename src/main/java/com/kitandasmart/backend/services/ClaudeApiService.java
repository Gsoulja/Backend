package com.kitandasmart.backend.services;

import com.kitandasmart.backend.dto.StoreDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ClaudeApiService {

    private final WebClient webClient;
    private final String apiKey;

    public ClaudeApiService(@Value("${anthropic.api.key}") String apiKey) {
        this.apiKey = apiKey;
        this.webClient = WebClient.builder()
                .baseUrl("https://api.anthropic.com")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("x-api-key", apiKey)
                .defaultHeader("anthropic-version", "2023-06-01")
                .build();
    }

    public String generateCompletion(String userMessage, String model) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);

        // Create messages array with user message
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "user", "content", userMessage));
        requestBody.put("messages", messages);

        // The parameter name changed from max_tokens_to_sample to max_tokens
        requestBody.put("max_tokens", 1000);

        return webClient.post()
                .uri("/v1/messages")  // New endpoint
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}