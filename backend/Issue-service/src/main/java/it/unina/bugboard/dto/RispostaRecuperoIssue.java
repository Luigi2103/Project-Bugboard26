package it.unina.bugboard.dto;

import java.util.List;

public class RispostaRecuperoIssue {
    private boolean success;
    private String message;
    private List<IssueDTO> issues;

    public RispostaRecuperoIssue() {}

    public RispostaRecuperoIssue(boolean success, String message, List<IssueDTO> issues) {
        this.success = success;
        this.message = message;
        this.issues = issues;
    }

    // Getters e Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<IssueDTO> getIssues() {
        return issues;
    }

    public void setIssues(List<IssueDTO> issues) {
        this.issues = issues;
    }
}