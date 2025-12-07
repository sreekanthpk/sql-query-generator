package com.sqlgen;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
public class SqlGeneratorService {

    private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${openai.api.key:}")
    private String configuredApiKey;

    public String generate(@NotBlank String prompt) {
        if (!StringUtils.hasText(prompt)) {
            return "";
        }

        String apiKey = resolveApiKey();


        try {
            Map<String, Object> systemMsg = Map.of(
                    "role", "system",
                    "content", "You are a SQL generator. Respond with a single valid SQL statement (no explanation, no surrounding backticks). Use standard SQL."
            );
            Map<String, Object> userMsg = Map.of(
                    "role", "user",
                    "content", "Convert the following prompt to SQL:\n" + prompt
            );

            Map<String, Object> body = Map.of(
                    "model", "gpt-3.5-turbo",
                    "messages", List.of(systemMsg, userMsg),
                    "temperature", 0.0,
                    "max_tokens", 512
            );

            String requestBody = mapper.writeValueAsString(body);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(OPENAI_URL))
                    .timeout(Duration.ofSeconds(30))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();



            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());


            JsonNode root = mapper.readTree(response.body());
            JsonNode contentNode = root.path("choices").path(0).path("message").path("content");
            String sql = contentNode.isMissingNode() ? "" : contentNode.asText("").trim();


            return sql;
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw  new RuntimeException(e);
        }
    }

    private String resolveApiKey() {
        if (StringUtils.hasText(configuredApiKey)) {
            return configuredApiKey;
        }
        String env = System.getenv("OPENAI_API_KEY");
        return StringUtils.hasText(env) ? env : "";
    }
}
