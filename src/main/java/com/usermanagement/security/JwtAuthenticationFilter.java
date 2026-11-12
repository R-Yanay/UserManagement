package com.usermanagement.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter fuer JWT-Authentifizierung.
 * Prueft jede Anfrage, ob ein gueltiger Token im Authorization-Header vorhanden ist.
 * Der Filter ist wie ein Tuersteher, der jeden Besucher kontrolliert.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Wird bei jeder HTTP-Anfrage aufgerufen, bevor der Controller erreicht wird.
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // Nur bei geschuetzten Endpunkten pruefen
        final String authHeader = request.getHeader("Authorization");
        final String token;
        final String userEmail;

        // Schritt 1: Authorization-Header pruefen
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // Kein Token vorhanden – Anfrage weiterleiten (vielleicht ist der Endpunkt oeffentlich)
            filterChain.doFilter(request, response);
            return;
        }

        // Schritt 2: Token aus dem Header extrahieren
        // Format: "Bearer eyJhbGciOiJIUzI1NiJ9..."
        token = authHeader.substring(7);  // "Bearer " hat 7 Zeichen

        // Schritt 3: E-Mail aus dem Token extrahieren
        userEmail = jwtService.extractUsername(token);

        // Schritt 4: Wenn die E-Mail vorhanden ist und noch nicht authentifiziert
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Benutzerdetails aus der Datenbank laden
            UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

            // Token pruefen: Ist er gueltig und gehoert zu diesem Benutzer?
            if (jwtService.isTokenValid(token, userDetails.getUsername())) {
                // Authentifizierung erstellen
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Authentifizierung im Security Context speichern
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Schritt 5: Anfrage weiterleiten zum naechsten Filter oder Controller
        filterChain.doFilter(request, response);
    }
}
