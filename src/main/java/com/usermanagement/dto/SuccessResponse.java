package com.usermanagement.dto;

/**
 * DTO fuer Erfolgsantworten.
 * Wird verwendet, um dem Client eine klare Erfolgsmeldung zurueckzugeben.
 */
public class SuccessResponse {

    private String message;

    public SuccessResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
