package it.unina.bugboard.common;

public class SessionManager {
    private String token;
    private String username;
    private boolean admin;

    public SessionManager() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public boolean isLoggedIn() {
        return token != null;
    }

    public void logout() {
        this.token = null;
        this.username = null;
        this.admin = false;
    }
}
