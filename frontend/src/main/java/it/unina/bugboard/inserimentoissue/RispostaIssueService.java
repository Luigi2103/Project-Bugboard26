package it.unina.bugboard.inserimentoissue;

import java.util.Map;

public class RispostaIssueService {

    private boolean success;
    private String message;
    private Map<String,String> fieldErrors;


    public RispostaIssueService() {}

    public RispostaIssueService(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public RispostaIssueService(boolean success, String message, Map<String, String> fieldErrors) {
        this.success = success;
        this.message = message;
        this.fieldErrors = fieldErrors;
    }


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

    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }

    public void setFieldErrors(Map<String, String> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }
}
