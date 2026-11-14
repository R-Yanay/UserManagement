package com.usermanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Datenbehälter fuer das Aendern des Passworts.
 * Enthält das alte Passwort zur Bestätigung sowie das neue Passwort und die Bestätigung.
 */
public class ChangePasswordRequest {

    @NotBlank(message = "Altes Passwort ist erforderlich")
    private String oldPassword;

    @NotBlank(message = "Neues Passwort ist erforderlich")
    @Size(min = 6, message = "Neues Passwort muss mindestens 6 Zeichen lang sein")
    private String newPassword;

    @NotBlank(message = "Passwort-Bestaetigung ist erforderlich")
    private String confirmPassword;

    public ChangePasswordRequest() {
    }

    public ChangePasswordRequest(String oldPassword, String newPassword, String confirmPassword) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
