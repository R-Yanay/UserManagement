package com.usermanagement.repository;

import com.usermanagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repository fuer Benutzer-Datenbank-Zugriff.
 * Spring Data JPA generiert den SQL-Code automatisch.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Findet einen Benutzer nach E-Mail-Adresse.
     */
    Optional<User> findByEmail(String email);

    /**
     * Prueft, ob eine E-Mail-Adresse bereits existiert.
     */
    boolean existsByEmail(String email);
}
