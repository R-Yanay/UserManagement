package com.usermanagement.dto;

import com.usermanagement.model.Role;

/**
 * Datenbehälter für die Login-Antwort.
 * Enthält die Daten, die der Server an den Client zurücksendet, inklusive JWT-Token.
 */
public class UserLoginResponse {

    private Long id;
    private String name;
    private String email;
    private Role role;
    private String message;
    private String token;

    public UserLoginResponse() {
    }

    public UserLoginResponse(Long id, String name, String email, Role role, String message, String token) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
        this.message = message;
        this.token = token;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
