package it.unina.bugboard.recovery;

import java.net.URI;
import java.net.http.HttpClient;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public class RecoveryApiService {

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RecoveryRespond updateApi(String username, String password) throws Exception {

        Map<String, String> UpdateData = new HashMap<>();
        UpdateData.put("username", username);
        UpdateData.put("password", password);

        String json = objectMapper.writeValueAsString(UpdateData);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/api/auth/update"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return objectMapper.readValue(response.body(), RecoveryRespond.class);
    }

}
