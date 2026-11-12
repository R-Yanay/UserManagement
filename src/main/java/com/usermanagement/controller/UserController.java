package com.usermanagement.controller;

import com.usermanagement.dto.ErrorResponse;
import com.usermanagement.dto.UserLoginRequest;
import com.usermanagement.dto.UserLoginResponse;
import com.usermanagement.dto.UserProfileResponse;
import com.usermanagement.dto.UserRegistrationRequest;
import com.usermanagement.dto.UserRegistrationResponse;
import com.usermanagement.exception.EmailAlreadyExistsException;
import com.usermanagement.exception.InvalidCredentialsException;
import com.usermanagement.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

/**
 * REST-Controller fuer Benutzerregistrierung, Login und Profil.
 * Beantwortet die URLs POST /api/users/register, POST /api/users/login und GET /api/users/profile.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Registriert einen neuen Benutzer.
     *
     * @param request Die Registrierungsdaten als JSON
     * @return HTTP 201 mit den Benutzerdaten bei Erfolg
     *         HTTP 409 wenn die E-Mail bereits registriert ist
     */
    @PostMapping("/register")
    public ResponseEntity<UserRegistrationResponse> register(@Valid @RequestBody UserRegistrationRequest request) {
        try {
            UserRegistrationResponse response = userService.registerUser(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (EmailAlreadyExistsException e) {
            // Wenn E-Mail bereits existiert, returniere 409 Conflict mit Fehlermeldung
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(null);
        }
    }

    /**
     * Loggt einen Benutzer ein.
     *
     * @param request Die Login-Daten als JSON (E-Mail, Passwort)
     * @return HTTP 200 mit den Benutzerdaten bei Erfolg
     *         HTTP 401 wenn E-Mail oder Passwort falsch sind
     */
    @PostMapping("/login")
    public ResponseEntity<UserLoginResponse> login(@Valid @RequestBody UserLoginRequest request) {
        try {
            UserLoginResponse response = userService.login(request);
            return ResponseEntity.ok(response);
        } catch (InvalidCredentialsException e) {
            // Wenn E-Mail oder Passwort falsch, returniere 401 Unauthorized
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    /**
     * Gibt das Profil des aktuell eingeloggten Benutzers zurueck.
     * Dieser Endpunkt ist geschuetzt - nur mit gueltigem JWT-Token erreichbar.
     *
     * @return HTTP 200 mit den Benutzerdaten des eingeloggten Benutzers (ohne Passwort)
     */
    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getProfile() {
        UserProfileResponse profile = userService.getProfile();
        return ResponseEntity.ok(profile);
    }

    /**
     * Globaler Fehler-Handler fuer Validierungsfehler.
     * Gibt eine klare Fehlermeldung als JSON zurueck.
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        ErrorResponse error = new ErrorResponse(e.getMessage());
        return ResponseEntity.badRequest().body(error);
    }
}
