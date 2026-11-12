# UserManagement API

Eine Spring Boot-Anwendung für das Benutzermanagement mit JWT-Authentifizierung.

## Funktionen

- **Benutzerregistrierung** mit E-Mail-Validierung und Passwort-Hashing (BCrypt)
- **Login** mit JWT-Token-Ausgabe
- **Geschützter Profil-Endpunkt** mit JWT-Authentifizierung
- **Eingabevalidierung** mit Jakarta Validation
- **Integrationstests** mit MockMvc und in-memory SQLite

## Technologien

- **Spring Boot 3.x** mit Java 25
- **Spring Security** mit JWT-Authentifizierung
- **Hibernate/JPA** mit SQLite-Datenbank
- **jjwt 0.12.x** für JWT-Token
- **Jakarta Validation** für DTO-Validierung
- **Maven** als Build-Tool

## Projektstruktur

```
src/
├── main/
│   ├── java/com/usermanagement/
│   │   ├── config/          # Security-Konfiguration
│   │   ├── controller/      # REST-Controller
│   │   ├── dto/             # Data Transfer Objects
│   │   ├── model/           # JPA-Entitäten
│   │   ├── repository/      # Datenzugriff
│   │   ├── security/        # JWT-Filter und Service
│   │   └── service/         # Geschäftslogik
│   └── resources/
│       └── application.properties
└── test/
    ├── java/com/usermanagement/
    │   └── controller/      # Integrationstests
    └── resources/
        └── application.properties
```

## Voraussetzungen

- **Java 25** (oder höher)
- **Maven 3.x**

## Build & Ausführung

### Projekt bauen

```bash
mvn clean install
```

### Anwendung starten

```bash
mvn spring-boot:run
```

Die Anwendung startet standardmäßig auf `http://localhost:8080`.

### Tests ausführen

```bash
mvn clean test
```

Alle 12 Integrationstests werden ausgeführt (11 API-Tests + 1 Context-Test).

## API-Endpunkte

### Registrierung

```
POST /api/users/register
Content-Type: application/json

{
  "name": "Max Mustermann",
  "email": "max@example.com",
  "password": "geheim123"
}
```

**Antwort:** `201 Created`

```json
{
  "message": "Benutzer erfolgreich registriert",
  "email": "max@example.com"
}
```

**Fehler:**
- `400 Bad Request` – Ungültige Eingabe (leerer Name, ungültige E-Mail, kurzes Passwort)
- `409 Conflict` – E-Mail bereits registriert

### Login

```
POST /api/users/login
Content-Type: application/json

{
  "email": "max@example.com",
  "password": "geheim123"
}
```

**Antwort:** `200 OK`

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

**Fehler:**
- `400 Bad Request` – Ungültige Eingabe
- `401 Unauthorized` – Falsche E-Mail oder Passwort

### Profil anzeigen (geschützt)

```
GET /api/users/profile
Authorization: Bearer <JWT-Token>
```

**Antwort:** `200 OK`

```json
{
  "name": "Max Mustermann",
  "email": "max@example.com"
}
```

**Fehler:**
- `401 Unauthorized` – Token fehlt oder abgelaufen
- `403 Forbidden` – Token ungültig

## Konfiguration

### JWT-Secret

Das JWT-Secret kann über eine Environment-Variable gesetzt werden:

```bash
export JWT_SECRET="dein-geheimer-schluessel"
```

Falls nicht gesetzt, wird der Standardwert aus `application.properties` verwendet (nicht für Produktion empfohlen).

### Datenbank

Standardmäßig wird eine SQLite-Datenbank (`users.db`) verwendet. Für die Produktion kann die Datenbank in `application.properties` geändert werden.

## Sicherheit

- Passwörter werden mit **BCrypt** gehasht
- JWT-Token werden mit **HS256** signiert
- Empfindliche Daten (Passwort) werden über `@JsonIgnore` aus der API-Antwort ausgeschlossen
- DTOs werden verwendet, um Entities nicht direkt zu exponieren

## Tests

Die Anwendung verwendet **Integrationstests** mit `@SpringBootTest` und `@AutoConfigureMockMvc`. Jeder Test läuft mit einer isolierten in-memory SQLite-Datenbank.

### Testabdeckung

| Kategorie | Tests |
|-----------|-------|
| Registrierung | 5 Tests (erfolgreich, doppelte E-Mail, leerer Name, kurzes Passwort, ungültige E-Mail) |
| Login | 4 Tests (erfolgreich, falsches Passwort, nicht registriert, leere E-Mail) |
| Profil | 2 Tests (ohne Token, mit gültigem Token) |
| Anwendung | 1 Test (Context lädt) |

## Lizenz

Dieses Projekt ist nur zu Lernzwecken erstellt.
