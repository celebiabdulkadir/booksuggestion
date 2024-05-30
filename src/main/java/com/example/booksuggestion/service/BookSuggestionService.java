package com.example.booksuggestion.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class BookSuggestionService {

    @Value("${openai.api.key}")
    private String apiKey;
    @Value("${openai.api.url}")
    private String apiUrl;

    public List<String> getRecommendations(Map<String, String> answers) {
        String prompt = generatePrompt(answers);
        RestTemplate restTemplate = new RestTemplate();

        // Create request payload
        String requestPayload = new JSONObject()
                .put("model", "gpt-3.5-turbo-instruct")
                .put("prompt", prompt)
                .put("max_tokens", 256)
                .put("temperature", 1)
                .put("top_p", 1)
                .put("frequency_penalty", 0)
                .put("presence_penalty", 0)
                .toString();

        // Create headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        // Create HTTP entity
        HttpEntity<String> entity = new HttpEntity<>(requestPayload, headers);

        // Send request and handle errors
        List<String> recommendations = new ArrayList<>();
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, entity, String.class);

            // Parse response
            if (response.getStatusCode() == HttpStatus.OK) {
                JSONObject jsonResponse = new JSONObject(response.getBody());
                JSONArray choices = jsonResponse.getJSONArray("choices");
                for (int i = 0; i < choices.length(); i++) {
                    recommendations.add(choices.getJSONObject(i).getString("text").trim());
                }
            } else {
                // Handle non-OK status codes
                handleErrorResponse(response);
            }
        } catch (HttpClientErrorException e) {
            // Handle client-side HTTP errors
            handleHttpClientError(e);
        } catch (Exception e) {
            // Handle other errors
            e.printStackTrace();
        }

        return recommendations;
    }

    private String generatePrompt(Map<String, String> answers) {
        System.out.println("Generating prompt with answers: " + answers);
        return "Suggest four books based on the following preferences:\n" +
                "Preferred Genre: " + answers.get("genre") + "\n" +
                "Favorite Author: " + answers.get("author") + "\n" +
                "Language: " + answers.get("language") + "\n";
    }

    private void handleErrorResponse(ResponseEntity<String> response) {
        // Log the error response or handle it accordingly
        System.err.println("Error response from OpenAI API: " + response.getStatusCode() + " - " + response.getBody());
    }

    private void handleHttpClientError(HttpClientErrorException e) {
        // Log the client-side HTTP error or handle it accordingly
        System.err.println("Client-side HTTP error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
    }
}