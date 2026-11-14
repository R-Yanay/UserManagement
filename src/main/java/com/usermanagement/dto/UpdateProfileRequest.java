package com.usermanagement.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Datenbehälter fuer das Aktualisieren des Benutzerprofils.
 * Enthält nur Name und E-Mail, da das Passwort über einen eigenen Endpunkt geändert wird.
 */
public class UpdateProfileRequest {

    @NotBlank(message = "Name ist erforderlich")
    private String name;

    @NotBlank(message = "E-Mail ist erforderlich")
    @Email(message = "Ungültige E-Mail-Adresse")
    private String email;

    public UpdateProfileRequest() {
    }

    public UpdateProfileRequest(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
