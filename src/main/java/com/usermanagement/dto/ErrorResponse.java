package com.usermanagement.dto;

/**
 * DTO fuer Fehlerantworten.
 * Wird verwendet, um dem Client eine klare Fehlermeldung zurueckzugeben.
 */
public class ErrorResponse {

    private String error;

    public ErrorResponse(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
