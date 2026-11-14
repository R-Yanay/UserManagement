package com.usermanagement.controller;

import com.usermanagement.dto.ChangePasswordRequest;
import com.usermanagement.dto.ErrorResponse;
import com.usermanagement.dto.SuccessResponse;
import com.usermanagement.dto.UpdateProfileRequest;
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
import org.springframework.web.bind.annotation.PutMapping;
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
    public ResponseEntity<?> register(@Valid @RequestBody UserRegistrationRequest request) {
        UserRegistrationResponse response = userService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Loggt einen Benutzer ein.
     *
     * @param request Die Login-Daten als JSON (E-Mail, Passwort)
     * @return HTTP 200 mit den Benutzerdaten bei Erfolg
     *         HTTP 401 wenn E-Mail oder Passwort falsch sind
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserLoginRequest request) {
        UserLoginResponse response = userService.login(request);
        return ResponseEntity.ok(response);
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
     * Aktualisiert das Profil des aktuell eingeloggten Benutzers (Name und E-Mail).
     * Dieser Endpunkt ist geschuetzt - nur mit gueltigem JWT-Token erreichbar.
     *
     * @param request Die neuen Profil-Daten als JSON (Name, E-Mail)
     * @return HTTP 200 mit den aktualisierten Benutzerdaten
     *         HTTP 409 wenn die neue E-Mail bereits von einem anderen Benutzer verwendet wird
     */
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        UserProfileResponse profile = userService.updateProfile(request);
        return ResponseEntity.ok(profile);
    }

    /**
     * Aendert das Passwort des aktuell eingeloggten Benutzers.
     * Dieser Endpunkt ist geschuetzt - nur mit gueltigem JWT-Token erreichbar.
     *
     * @param request Die Passwort-Aenderungsdaten als JSON (altes Passwort, neues Passwort, Bestaetigung)
     * @return HTTP 200 mit Bestaetigungsnachricht
     *         HTTP 401 wenn das alte Passwort falsch ist
     *         HTTP 400 wenn newPassword und confirmPassword nicht uebereinstimmen oder das neue Passwort zu kurz ist
     */
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        String message = userService.changePassword(request);
        return ResponseEntity.ok(new SuccessResponse(message));
    }

    /**
     * Fehler-Handler fuer doppelte E-Mail-Adresse.
     * Muss vor dem allgemeinen Exception-Handler stehen.
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyExists(EmailAlreadyExistsException e) {
        ErrorResponse error = new ErrorResponse(e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    /**
     * Fehler-Handler fuer ungueltige Login-Daten.
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(InvalidCredentialsException e) {
        ErrorResponse error = new ErrorResponse(e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
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
