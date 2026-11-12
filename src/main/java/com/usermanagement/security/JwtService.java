package com.usermanagement.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Service fuer JWT-Token.
 * Erstellt neue Tokens nach dem Login und prueft Tokens bei jeder Anfrage.
 */
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    /**
     * Erstellt den Verschlussel aus dem Secret aus application.properties.
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Extrahiert alle Claims (Daten) aus einem Token.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Extrahiert einen bestimmten Claim aus einem Token.
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extrahiert den Benutzernamen (E-Mail) aus einem Token.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Erstellt ein neues JWT-Token fuer einen Benutzer.
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return buildToken(claims, userDetails.getUsername());
    }

    /**
     * Baut das Token mit Claims, Subject (E-Mail) und Gueltigkeitsdauer.
     */
    private String buildToken(Map<String, Object> claims, String username) {
        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Prueft, ob ein Token gueltig ist und dem Benutzer gehoert.
     * Wenn der Token abgelaufen ist, wirft jjwt bereits eine ExpiredJwtException,
     * die hier als JwtException abgefangen wird.
     */
    public boolean isTokenValid(String token, String userEmail) {
        try {
            String username = extractUsername(token);
            return username.equals(userEmail);
        } catch (JwtException e) {
            return false;
        }
    }
}
