package com.usermanagement.service;

import com.usermanagement.dto.ChangePasswordRequest;
import com.usermanagement.dto.UpdateProfileRequest;
import com.usermanagement.dto.UserLoginRequest;
import com.usermanagement.dto.UserLoginResponse;
import com.usermanagement.dto.UserProfileResponse;
import com.usermanagement.dto.UserRegistrationRequest;
import com.usermanagement.dto.UserRegistrationResponse;
import com.usermanagement.exception.EmailAlreadyExistsException;
import com.usermanagement.exception.InvalidCredentialsException;
import com.usermanagement.model.Role;
import com.usermanagement.model.User;
import com.usermanagement.repository.UserRepository;
import com.usermanagement.security.JwtService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service fuer Benutzerregistrierung und Login.
 * Enthaelte die Geschaeftslogik: E-Mail-Pruefung, Passwort-Hashing, Speichern und JWT-Token-Erstellung.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserService(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.jwtService = jwtService;
    }

    /**
     * Registriert einen neuen Benutzer.
     *
     * @param request Die Registrierungsdaten (Name, E-Mail, Passwort)
     * @return Eine Antwort mit den Benutzerdaten und einer Bestaetigungsnachricht
     * @throws RuntimeException Wenn die E-Mail bereits registriert ist
     */
    public UserRegistrationResponse registerUser(UserRegistrationRequest request) {

        // Schritt 1: Pruefen, ob die E-Mail bereits existiert
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("E-Mail-Adresse ist bereits registriert: " + request.getEmail());
        }

        // Schritt 2: Passwort mit BCrypt hashen
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        // Schritt 3: Neues User-Objekt erstellen (mit Role.USER als Standard)
        User user = new User(request.getName(), request.getEmail(), hashedPassword, Role.USER);

        // Schritt 4: Benutzer in der Datenbank speichern
        User savedUser = userRepository.save(user);

        // Schritt 5: Antwort zurueckgeben
        return new UserRegistrationResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getRole(),
                "Benutzer erfolgreich registriert!"
        );
    }

    /**
     * Loggt einen Benutzer ein und gibt ein JWT-Token zurueck.
     *
     * @param request Die Login-Daten (E-Mail, Passwort)
     * @return Eine Antwort mit den Benutzerdaten, einem JWT-Token und einer Bestaetigungsnachricht
     * @throws RuntimeException Wenn E-Mail oder Passwort falsch sind
     */
    public UserLoginResponse login(UserLoginRequest request) {

        // Schritt 1: Benutzer nach E-Mail suchen
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("E-Mail oder Passwort ist falsch"));

        // Schritt 2: Passwort mit BCrypt vergleichen
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("E-Mail oder Passwort ist falsch");
        }

        // Schritt 3: JWT-Token erstellen
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();
        String token = jwtService.generateToken(userDetails);

        // Schritt 4: Antwort mit Token zurueckgeben
        return new UserLoginResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                "Login erfolgreich!",
                token
        );
    }

    /**
     * Gibt das Profil des aktuell eingeloggten Benutzers zurueck.
     * Die E-Mail wird automatisch aus dem SecurityContext geholt,
     * den der JwtAuthenticationFilter beim Pruefen des Tokens gefuellt hat.
     *
     * @return Ein DTO mit den Benutzerdaten (ohne Passwort)
     * @throws RuntimeException Wenn kein Benutzer eingeloggt ist
     */
    public UserProfileResponse getProfile() {
        // Schritt 1: E-Mail aus dem SecurityContext holen
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        // Schritt 2: Benutzer aus der Datenbank laden
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden: " + email));

        // Schritt 3: Als DTO zurueckgeben (ohne Passwort)
        return new UserProfileResponse(user.getId(), user.getName(), user.getEmail(), user.getRole(), user.getCreatedAt());
    }

    /**
     * Aktualisiert das Profil des aktuell eingeloggten Benutzers (Name und E-Mail).
     * Wenn die neue E-Mail bereits von einem anderen Benutzer verwendet wird, wird eine Exception geworfen.
     *
     * @param request Die neuen Profil-Daten (Name, E-Mail)
     * @return Ein DTO mit den aktualisierten Benutzerdaten (ohne Passwort)
     * @throws EmailAlreadyExistsException Wenn die neue E-Mail bereits von einem anderen Benutzer verwendet wird
     */
    public UserProfileResponse updateProfile(UpdateProfileRequest request) {
        // Schritt 1: E-Mail des aktuell eingeloggten Benutzers holen
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        // Schritt 2: Pruefen, ob die neue E-Mail bereits von einem anderen Benutzer verwendet wird
        userRepository.findByEmail(request.getEmail()).ifPresent(otherUser -> {
            if (!otherUser.getEmail().equals(currentEmail)) {
                throw new EmailAlreadyExistsException("E-Mail-Adresse ist bereits registriert: " + request.getEmail());
            }
        });

        // Schritt 3: aktuellen Benutzer aus der Datenbank laden
        User user = userRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden: " + currentEmail));

        // Schritt 4: Name und E-Mail aktualisieren
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        userRepository.save(user);

        // Schritt 5: Aktualisiertes Profil zurueckgeben
        return new UserProfileResponse(user.getId(), user.getName(), user.getEmail(), user.getRole(), user.getCreatedAt());
    }

    /**
     * Aendert das Passwort des aktuell eingeloggten Benutzers.
     * Das alte Passwort muss stimmen, das neue Passwort muss mindestens 6 Zeichen lang sein
     * und muss mit der Bestaetigung uebereinstimmen.
     *
     * @param request Die Passwort-Aenderungsdaten (altes Passwort, neues Passwort, Bestaetigung)
     * @return Eine Bestaetigungsnachricht
     * @throws InvalidCredentialsException Wenn das alte Passwort falsch ist
     * @throws RuntimeException Wenn newPassword und confirmPassword nicht uebereinstimmen oder das neue Passwort zu kurz ist
     */
    public String changePassword(ChangePasswordRequest request) {
        // Schritt 1: E-Mail des aktuell eingeloggten Benutzers holen
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        // Schritt 2: Benutzer aus der Datenbank laden
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Benutzer nicht gefunden: " + email));

        // Schritt 3: Altes Passwort pruefen
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Altes Passwort ist falsch");
        }

        // Schritt 4: Neues Passwort mit Bestaetigung vergleichen
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Neues Passwort und Bestaetigung stimmen nicht ueberein");
        }

        // Schritt 5: Neues Passwort muss mindestens 6 Zeichen lang sein
        if (request.getNewPassword().length() < 6) {
            throw new RuntimeException("Neues Passwort muss mindestens 6 Zeichen lang sein");
        }

        // Schritt 6: Neues Passwort mit BCrypt hashen und speichern
        String hashedPassword = passwordEncoder.encode(request.getNewPassword());
        user.setPassword(hashedPassword);
        userRepository.save(user);

        // Schritt 7: Bestaetigungsnachricht zurueckgeben
        return "Passwort erfolgreich geändert!";
    }
}
