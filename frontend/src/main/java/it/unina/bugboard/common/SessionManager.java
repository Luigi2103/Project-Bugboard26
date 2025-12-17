package it.unina.bugboard.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Base64;

public class SessionManager {
    private static final Logger log = LoggerFactory.getLogger(SessionManager.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private String token;
    private String username;
    private boolean admin;
    private Long userId;
    private Long expirationTime;
    private String nome;
    private String cognome;

    public SessionManager() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
        if (token != null && !token.isEmpty()) {
            decodeJwt(token);
        } else {
            clearUserData();
        }
    }

    private void decodeJwt(String token) {
        try {
            String[] chunks = token.split("\\.");
            if (chunks.length < 2) {
                log.warn("Token JWT non valido: formato errato");
                return;
            }

            Base64.Decoder decoder = Base64.getUrlDecoder();
            String payload = new String(decoder.decode(chunks[1]));

            JsonNode node = objectMapper.readTree(payload);

            this.username = node.has("sub") ? node.get("sub").asText() : null;
            this.userId = node.has("userId") ? node.get("userId").asLong() : null;
            this.admin = node.has("role") && "ADMIN".equalsIgnoreCase(node.get("role").asText());
            this.expirationTime = node.has("exp") ? node.get("exp").asLong() : null;
            this.nome = node.has("nome") ? node.get("nome").asText() : null;
            this.cognome = node.has("cognome") ? node.get("cognome").asText() : null;

        } catch (Exception e) {
            log.error("Errore durante la decodifica del JWT", e);
            clearUserData();
        }
    }

    private void clearUserData() {
        this.username = null;
        this.userId = null;
        this.admin = false;
        this.expirationTime = null;
        this.nome = null;
        this.cognome = null;
    }

    public String getUsername() {
        return username;
    }

    public boolean isAdmin() {
        return admin;
    }

    public Long getUserId() {
        return userId;
    }

    public boolean isLoggedIn() {
        return token != null && !isTokenExpired();
    }

    public boolean isTokenExpired() {
        if (expirationTime == null) {
            return false;
        }
        long currentTime = System.currentTimeMillis() / 1000;
        return currentTime >= expirationTime;
    }

    public void logout() {
        this.token = null;
        clearUserData();
    }

    public String getAuthorizationHeader() {
        return token != null ? "Bearer " + token : null;
    }

    public String getNome() {
        return nome;
    }

    public String getCognome() {
        return cognome;
    }
}