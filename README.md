# UserManagement API

Eine Spring Boot-Anwendung für das Benutzermanagement mit JWT-Authentifizierung.

## Funktionen

- **Benutzerregistrierung** mit E-Mail-Validierung und Passwort-Hashing (BCrypt)
- **Login** mit JWT-Token-Ausgabe
- **Geschützter Profil-Endpunkt** mit JWT-Authentifizierung
- **Eingabevalidierung** mit Jakarta Validation
- **HTML-Frontend** mit Registrierungs-, Login- und Profilseite
- **Integrationstests** mit MockMvc und in-memory SQLite

## Technologien

- **Spring Boot 3.x** mit Java 25
- **Spring Security** mit JWT-Authentifizierung
- **Hibernate/JPA** mit SQLite-Datenbank (dateibasiert)
- **jjwt 0.12.x** für JWT-Token
- **Jakarta Validation** für DTO-Validierung
- **HTML + CSS + JavaScript** für das Frontend
- **Maven** als Build-Tool

## Projektstruktur

```
src/
├── main/
│   ├── java/com/usermanagement/
│   │   ├── config/          # Security-, DataSource- und Web-Konfiguration
│   │   │   ├── SecurityConfig.java
│   │   │   ├── DataSourceConfig.java
│   │   │   └── WebConfig.java
│   │   ├── controller/      # REST-Controller
│   │   ├── dto/             # Data Transfer Objects
│   │   ├── model/           # JPA-Entitäten
│   │   ├── repository/      # Datenzugriff
│   │   ├── security/        # JWT-Filter und Service
│   │   └── service/         # Geschäftslogik
│   └── resources/
│       ├── application.properties
│       └── static/          # HTML-Frontend
│           ├── register.html
│           ├── login.html
│           └── profile.html
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

## HTML-Frontend

Die Anwendung enthält ein einfaches HTML-Frontend, das direkt im Browser genutzt werden kann.

### Frontend-Seiten

| Seite | URL | Beschreibung |
|-------|-----|-------------|
| Registrieren | `http://localhost:8080/register.html` | Benutzerregistrierung mit Name, E-Mail und Passwort |
| Login | `http://localhost:8080/login.html` | Anmeldung mit E-Mail und Passwort |
| Profil | `http://localhost:8080/profile.html` | Geschützte Profilseite (JWT-Token erforderlich) |

### So funktioniert das Frontend

1. **Registrierung:** Fülle das Formular aus und klicke auf "Registrieren". Bei Erfolg erscheint eine grüne Meldung.
2. **Login:** Gib E-Mail und Passwort ein. Nach erfolgreichem Login wird der JWT-Token im Browser (`localStorage`) gespeichert und automatisch zur Profilseite weitergeleitet.
3. **Profil:** Zeigt Name, E-Mail und Rolle des eingeloggten Benutzers an. Ohne Token wird zur Loginseite weitergeleitet.

### Fehlermeldungen

- **Doppelte E-Mail bei Registrierung:** Rote Fehlermeldung "E-Mail-Adresse ist bereits registriert: ..."
- **Falsche Login-Daten:** Rote Fehlermeldung "E-Mail oder Passwort ist falsch"
- **Server nicht erreichbar:** Fehlermeldung "Keine Verbindung zum Server"

## Konfiguration

### JWT-Secret

Das JWT-Secret kann über eine Environment-Variable gesetzt werden:

```bash
export JWT_SECRET="dein-geheimer-schluessel"
```

Falls nicht gesetzt, wird der Standardwert aus `application.properties` verwendet (nicht für Produktion empfohlen).

### Datenbank

Standardmäßig wird eine **dateibasierte SQLite-Datenbank** (`users.db`) verwendet. Die Datenbankdatei wird automatisch im Projektverzeichnis erstellt.

**Wichtig:** Bei einer in-memory SQLite (`jdbc:sqlite::memory:`) würde jede neue JDBC-Verbindung eine leere Datenbank erstellen, weshalb `DriverManagerDataSource` in `DataSourceConfig.java` manuell konfiguriert ist.

### Web-Konfiguration

`WebConfig.java` schaltet den Browser-Cache für HTML-Dateien aus, damit immer die neueste Version der Frontend-Seiten geladen wird.

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
