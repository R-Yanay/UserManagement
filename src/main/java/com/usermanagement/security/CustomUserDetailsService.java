package com.usermanagement.security;

import com.usermanagement.model.User;
import com.usermanagement.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Benutzer-Service fuer Spring Security.
 * Uebersetzt die eigene User-Entitaet in das UserDetails-Interface von Spring Security.
 * Wird vom JwtAuthenticationFilter verwendet, um Benutzerdetails aus der Datenbank zu laden.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Ladt die Benutzerdetails basierend auf der E-Mail-Adresse.
     * Spring Security verwendet diese Methode, um den Benutzer zu finden.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Benutzer nicht gefunden: " + email));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();
    }
}
