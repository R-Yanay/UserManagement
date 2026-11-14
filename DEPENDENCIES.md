# Abhängigkeitsdiagramm – UserManagement

Übersicht aller Klassen und ihrer Abhängigkeiten im UserManagement-Projekt.

## Klassendiagramm (Mermaid)

```mermaid
graph TD
    subgraph Controller ["Controller-Schicht"]
        UC["UserController"]
    end

    subgraph Service ["Service-Schicht"]
        US["UserService"]
    end

    subgraph Config ["Konfiguration"]
        SC["SecurityConfig"]
        DSC["DataSourceConfig"]
        WC["WebConfig"]
    end

    subgraph Security ["Security-Schicht"]
        JWT["JwtService"]
        JWF["JwtAuthenticationFilter"]
        CUD["CustomUserDetailsService"]
    end

    subgraph Repository ["Repository-Schicht"]
        UR["UserRepository"]
    end

    subgraph Model ["Model-Schicht"]
        U["User"]
        R["Role"]
    end

    subgraph DTOs ["DTOs (Request)"]
        URR["UserRegistrationRequest"]
        ULReq["UserLoginRequest"]
        UPRReq["UpdateProfileRequest"]
        CPR["ChangePasswordRequest"]
    end

    subgraph DTOsResp ["DTOs (Response)"]
        URResp["UserRegistrationResponse"]
        ULResp["UserLoginResponse"]
        UPR["UserProfileResponse"]
        ER["ErrorResponse"]
        SR["SuccessResponse"]
    end

    subgraph Exceptions ["Custom Exceptions"]
        EAE["EmailAlreadyExistsException"]
        ICE["InvalidCredentialsException"]
    end

    subgraph App ["Anwendung"]
        UMA["UserManagementApplication"]
    end

    %% Controller -> Service
    UC -->|"nutzt"| US

    %% Controller -> DTOs (Request)
    UC -->|"empfängt"| URR
    UC -->|"empfängt"| ULReq
    UC -->|"empfängt"| UPRReq
    UC -->|"empfängt"| CPR

    %% Controller -> DTOs (Response)
    UC -->|"gibt zurück"| URResp
    UC -->|"gibt zurück"| ULResp
    UC -->|"gibt zurück"| UPR
    UC -->|"gibt zurück"| ER
    UC -->|"gibt zurück"| SR

    %% Controller -> Exceptions
    UC -->|"fängt ab"| EAE
    UC -->|"fängt ab"| ICE

    %% Service -> Repository
    US -->|"persistiert & liest"| UR

    %% Service -> Security
    US -->|"generiert Token"| JWT

    %% Service -> Model
    US -->|"erstellt"| U

    %% Service -> DTOs (Request)
    US -->|"liest"| URR
    US -->|"liest"| ULReq
    US -->|"liest"| UPRReq
    US -->|"liest"| CPR

    %% Service -> DTOs (Response)
    US -->|"gibt zurück"| URResp
    US -->|"gibt zurück"| ULResp
    US -->|"gibt zurück"| UPR

    %% Service -> Exceptions
    US -->|"wirft"| EAE
    US -->|"wirft"| ICE

    %% Security Filter -> JwtService
    JWF -->|"validiert Token"| JWT

    %% Security Filter -> CustomUserDetailsService
    JWF -->|"lädt Benutzer"| CUD

    %% SecurityConfig -> JwtAuthenticationFilter
    SC -->|"registriert Filter"| JWF

    %% DataSourceConfig -> DataSource
    DSC -->|"konfiguriert"| DS["DataSource (SQLite)"]

    %% WebConfig -> Cache-Control
    WC -->|"Cache-Control-Header"| HTML["HTML-Frontend"]

    %% CustomUserDetailsService -> Repository
    CUD -->|"liest Benutzer"| UR

    %% CustomUserDetailsService -> Model
    CUD -->|"konvertiert"| U

    %% Repository -> Model
    UR -->|"speichert & liest"| U

    %% Model -> Model
    U -->|"hat Rolle"| R

    %% Application -> alles (Spring Boot Entry Point)
    UMA -.->|"startet"| UC
    UMA -.->|"startet"| US
    UMA -.->|"startet"| SC
```

## Abhängigkeiten nach Schichten

### Controller-Schicht

| Klasse | Abhängigkeiten |
|--------|---------------|
| `UserController` | `UserService`, `UserRegistrationRequest`, `UserLoginRequest`, `UpdateProfileRequest`, `ChangePasswordRequest`, `UserRegistrationResponse`, `UserLoginResponse`, `UserProfileResponse`, `ErrorResponse`, `SuccessResponse`, `EmailAlreadyExistsException`, `InvalidCredentialsException` |

### Service-Schicht

| Klasse | Abhängigkeiten |
|--------|---------------|
| `UserService` | `UserRepository`, `JwtService`, `User`, `Role`, `UserRegistrationRequest`, `UserLoginRequest`, `UpdateProfileRequest`, `ChangePasswordRequest`, `UserRegistrationResponse`, `UserLoginResponse`, `UserProfileResponse`, `EmailAlreadyExistsException`, `InvalidCredentialsException` |

### Konfiguration

| Klasse | Abhängigkeiten |
|--------|---------------|
| `SecurityConfig` | `JwtAuthenticationFilter` |
| `DataSourceConfig` | `application.properties` (über `@Value`) – konfiguriert SQLite-DataSource mit `DriverManagerDataSource` |
| `WebConfig` | *(keine internen)* – setzt Cache-Control-Header für HTML-Dateien |

### Security-Schicht

| Klasse | Abhängigkeiten |
|--------|---------------|
| `JwtService` | *(keine internen)* – nur jjwt und Spring Security |
| `JwtAuthenticationFilter` | `JwtService`, `CustomUserDetailsService` (über `UserDetailsService` Interface) |
| `CustomUserDetailsService` | `UserRepository`, `User` |

### Repository-Schicht

| Klasse | Abhängigkeiten |
|--------|---------------|
| `UserRepository` | `User` (JPA-Interface, `JpaRepository<User, Long>`) |

### Model-Schicht

| Klasse | Abhängigkeiten |
|--------|---------------|
| `User` | `Role` (Enum-Feld `role`) |
| `Role` | *(keine)* – reines Enum |

### DTOs

| Klasse | Abhängigkeiten |
|--------|---------------|
| `UserRegistrationRequest` | *(keine internen)* – nur Jakarta Validation |
| `UserLoginRequest` | *(keine internen)* – nur Jakarta Validation |
| `UpdateProfileRequest` | *(keine internen)* – nur Jakarta Validation (`@NotBlank`, `@Email`) |
| `ChangePasswordRequest` | *(keine internen)* – nur Jakarta Validation (`@NotBlank`, `@Size(min=6)`) |
| `UserRegistrationResponse` | `Role` (Enum-Feld) |
| `UserLoginResponse` | `Role` (Enum-Feld) |
| `UserProfileResponse` | `Role` (Enum-Feld), `LocalDateTime` (`createdAt`) |
| `ErrorResponse` | *(keine)* |
| `SuccessResponse` | *(keine)* – DTO für Erfolgsantworten mit `message`-Feld |

### Custom Exceptions

| Klasse | Abhängigkeiten |
|--------|---------------|
| `EmailAlreadyExistsException` | *(keine)* – erweitert `RuntimeException` |
| `InvalidCredentialsException` | *(keine)* – erweitert `RuntimeException` |

### HTML-Frontend

| Datei | Beschreibung |
|-------|-------------|
| `register.html` | Registrierungsformular – sendet POST an `/api/users/register` via `fetch()` |
| `login.html` | Login-Formular – sendet POST an `/api/users/login`, speichert JWT-Token in `localStorage` |
| `profile.html` | Profilseite – zeigt Name, E-Mail, Rolle und Registrierungsdatum; enthält "Bearbeiten"-Formular (PUT `/api/users/profile`) und "Passwort ändern"-Formular (POST `/api/users/change-password`) |

## Datenfluss bei Registrierung

```
Client → POST /api/users/register
  → UserController.register(UserRegistrationRequest)
    → UserService.registerUser(UserRegistrationRequest)
      → UserRepository.existsByEmail(email)
      → UserRepository.save(User)
      → UserRegistrationResponse
    → ResponseEntity<UserRegistrationResponse> (201 Created)
```

## Datenfluss bei Login

```
Client → POST /api/users/login
  → UserController.login(UserLoginRequest)
    → UserService.login(UserLoginRequest)
      → UserRepository.findByEmail(email)
      → BCryptPasswordEncoder.matches()
      → JwtService.generateToken()
      → UserLoginResponse (mit Token)
    → ResponseEntity<UserLoginResponse> (200 OK)
```

## Datenfluss bei Profilabfrage (geschützt)

```
Client → GET /api/users/profile (mit JWT-Token)
  → JwtAuthenticationFilter (prüft Token)
    → JwtService.extractUsername(token)
    → CustomUserDetailsService.loadUserByUsername(email)
      → UserRepository.findByEmail(email)
    → JwtService.isTokenValid(token, email)
    → SecurityContextHolder.setAuthentication()
  → UserController.getProfile()
    → UserService.getProfile()
      → SecurityContextHolder.getContext().getAuthentication().getName()
      → UserRepository.findByEmail(email)
      → UserProfileResponse (mit createdAt)
    → ResponseEntity<UserProfileResponse> (200 OK)
```

## Datenfluss bei Profil bearbeiten (geschützt)

```
Client → PUT /api/users/profile (mit JWT-Token + UpdateProfileRequest)
  → JwtAuthenticationFilter (prüft Token)
    → SecurityContextHolder.setAuthentication()
  → UserController.updateProfile(UpdateProfileRequest)
    → UserService.updateProfile(UpdateProfileRequest)
      → SecurityContextHolder.getContext().getAuthentication().getName()
      → UserRepository.findByEmail(email)
      → UserRepository.existsByEmail(neueEmail) (Prüfung auf doppelte E-Mail)
      → User.setName(), User.setEmail()
      → UserRepository.save(User)
      → UserProfileResponse (mit createdAt)
    → ResponseEntity<UserProfileResponse> (200 OK)
    → ODER ResponseEntity<ErrorResponse> (409 Conflict bei doppelter E-Mail)
```

## Datenfluss bei Passwort ändern (geschützt)

```
Client → POST /api/users/change-password (mit JWT-Token + ChangePasswordRequest)
  → JwtAuthenticationFilter (prüft Token)
    → SecurityContextHolder.setAuthentication()
  → UserController.changePassword(ChangePasswordRequest)
    → UserService.changePassword(ChangePasswordRequest)
      → SecurityContextHolder.getContext().getAuthentication().getName()
      → UserRepository.findByEmail(email)
      → BCryptPasswordEncoder.matches(oldPassword) (Prüfung altes Passwort)
      → newPassword == confirmPassword (Prüfung Übereinstimmung)
      → BCryptPasswordEncoder.encode(newPassword)
      → User.setPassword()
      → UserRepository.save(User)
    → ResponseEntity<SuccessResponse> (200 OK mit "Passwort erfolgreich geändert!")
    → ODER ResponseEntity<ErrorResponse> (401 bei falschem altem Passwort)
    → ODER ResponseEntity<ErrorResponse> (400 bei Validierungsfehlern)
```

---

## HTTP-Request-Flow Diagramme

Diese Mermaid-Sequenzdiagramme zeigen detailliert, wie eine HTTP-Anfrage vom Browser durch alle Schichten von Spring Boot läuft.

### 1. Registrierung (POST `/api/users/register`)

```mermaid
sequenceDiagram
    participant Browser as Browser (register.html)
    participant Tomcat as Tomcat (Spring Boot)
    participant Security as Spring Security Filter
    participant Controller as UserController
    participant Service as UserService
    participant Repo as UserRepository
    participant DB as SQLite (users.db)

    Browser->>Tomcat: POST /api/users/register<br/>Content-Type: application/json<br/>{name, email, password}
    Tomcat->>Security: Filter-Kette durchlaufen
    Security-->>Tomcat: permitAll() – Endpunkt öffentlich
    Tomcat->>Controller: register(UserRegistrationRequest)
    Controller->>Service: registerUser(UserRegistrationRequest)
    Service->>Repo: existsByEmail(email)
    Repo->>DB: SELECT id FROM users WHERE email=? LIMIT 1
    DB-->>Repo: false (E-Mail frei)
    Repo-->>Service: false
    Service->>Service: User erstellen<br/>BCrypt.encode(password)<br/>createdAt = now()
    Service->>Repo: save(User)
    Repo->>DB: INSERT INTO users VALUES (...)
    DB-->>Repo: User mit ID
    Repo-->>Service: User gespeichert
    Service->>Service: UserRegistrationResponse erstellen
    Service-->>Controller: UserRegistrationResponse
    Controller->>Controller: ResponseEntity.status(201).body(response)
    Controller-->>Tomcat: 201 Created + JSON
    Tomcat-->>Browser: HTTP 201<br/>{name, email, role}
    Browser->>Browser: Erfolgsmeldung anzeigen
```

### 2. Login (POST `/api/users/login`)

```mermaid
sequenceDiagram
    participant Browser as Browser (login.html)
    participant Tomcat as Tomcat (Spring Boot)
    participant Security as Spring Security Filter
    participant Controller as UserController
    participant Service as UserService
    participant Jwt as JwtService
    participant Repo as UserRepository
    participant DB as SQLite (users.db)

    Browser->>Tomcat: POST /api/users/login<br/>Content-Type: application/json<br/>{email, password}
    Tomcat->>Security: Filter-Kette durchlaufen
    Security-->>Tomcat: permitAll() – Endpunkt öffentlich
    Tomcat->>Controller: login(UserLoginRequest)
    Controller->>Service: login(UserLoginRequest)
    Service->>Repo: findByEmail(email)
    Repo->>DB: SELECT * FROM users WHERE email=?
    DB-->>Repo: User gefunden
    Repo-->>Service: User
    Service->>Service: BCrypt.matches(password, user.password)
    alt Passwort stimmt
        Service->>Jwt: generateToken(email)
        Jwt->>Jwt: JWS.sign() mit Secret
        Jwt-->>Service: JWT-Token String
        Service->>Service: UserLoginResponse(token, email, role)
        Service-->>Controller: UserLoginResponse
        Controller->>Controller: ResponseEntity.ok(response)
        Controller-->>Tomcat: 200 OK + JSON
        Tomcat-->>Browser: HTTP 200<br/>{token, email, role}
        Browser->>Browser: localStorage.setItem('token', token)<br/>Weiter zu profile.html
    else Passwort falsch
        Service-->>Controller: InvalidCredentialsException
        Controller->>Controller: ResponseEntity.status(401).body(error)
        Controller-->>Tomcat: 401 Unauthorized + JSON
        Tomcat-->>Browser: HTTP 401<br/>{error: "E-Mail oder Passwort ist falsch"}
        Browser->>Browser: Rote Fehlermeldung anzeigen
    end
```

### 3. Profil anzeigen (GET `/api/users/profile` – geschützt)

```mermaid
sequenceDiagram
    participant Browser as Browser (profile.html)
    participant Tomcat as Tomcat (Spring Boot)
    participant JwtFilter as JwtAuthenticationFilter
    participant Jwt as JwtService
    participant UserDetail as CustomUserDetailsService
    participant Security as SecurityContextHolder
    participant Controller as UserController
    participant Service as UserService
    participant Repo as UserRepository
    participant DB as SQLite (users.db)

    Browser->>Tomcat: GET /api/users/profile<br/>Authorization: Bearer <JWT-Token>
    Tomcat->>JwtFilter: doFilterInternal()
    JwtFilter->>Jwt: extractUsername(token)
    Jwt-->>JwtFilter: email aus Token
    JwtFilter->>Jwt: isTokenValid(token, email)
    alt Token gültig
        Jwt-->>JwtFilter: true
        JwtFilter->>UserDetail: loadUserByUsername(email)
        UserDetail->>Repo: findByEmail(email)
        Repo->>DB: SELECT * FROM users WHERE email=?
        DB-->>Repo: User
        Repo-->>UserDetail: User
        UserDetail-->>JwtFilter: UserDetails
        JwtFilter->>Security: setAuthentication(UsernamePasswordAuthenticationToken)
        Security-->>JwtFilter: Authentication gespeichert
        JwtFilter->>Controller: FilterChain.doFilter() – weiterleiten
        Controller->>Service: getProfile()
        Service->>Security: getContext().getAuthentication().getName()
        Security-->>Service: email
        Service->>Repo: findByEmail(email)
        Repo->>DB: SELECT * FROM users WHERE email=?
        DB-->>Repo: User
        Repo-->>Service: User
        Service->>Service: UserProfileResponse(name, email, role, createdAt)
        Service-->>Controller: UserProfileResponse
        Controller->>Controller: ResponseEntity.ok(profile)
        Controller-->>Tomcat: 200 OK + JSON
        Tomcat-->>Browser: HTTP 200<br/>{id, name, email, role, createdAt}
        Browser->>Browser: Profil anzeigen<br/>(Name, E-Mail, Rolle, Registrierungsdatum)
    else Token ungültig oder fehlt
        Jwt-->>JwtFilter: false
        JwtFilter-->>Tomcat: 403 Forbidden
        Tomcat-->>Browser: HTTP 403
        Browser->>Browser: Zur login.html weiterleiten
    end
```

### 4. Profil bearbeiten (PUT `/api/users/profile` – geschützt)

```mermaid
sequenceDiagram
    participant Browser as Browser (profile.html)
    participant Tomcat as Tomcat (Spring Boot)
    participant JwtFilter as JwtAuthenticationFilter
    participant Controller as UserController
    participant Service as UserService
    participant Repo as UserRepository
    participant DB as SQLite (users.db)

    Browser->>Tomcat: PUT /api/users/profile<br/>Authorization: Bearer <JWT-Token><br/>{name, email}
    Tomcat->>JwtFilter: doFilterInternal() – Token prüfen
    JwtFilter-->>Tomcat: Token gültig, weiterleiten
    Tomcat->>Controller: updateProfile(UpdateProfileRequest)
    Controller->>Service: updateProfile(UpdateProfileRequest)
    Service->>Service: Aktuelle email aus SecurityContext
    Service->>Repo: findByEmail(aktuelleEmail)
    Repo->>DB: SELECT * FROM users WHERE email=?
    DB-->>Repo: User
    Repo-->>Service: User
    alt Neue E-Mail ist bereits registriert
        Service->>Repo: existsByEmail(neueEmail)
        Repo->>DB: SELECT id FROM users WHERE email=? LIMIT 1
        DB-->>Repo: true
        Repo-->>Service: true
        Service-->>Controller: EmailAlreadyExistsException
        Controller->>Controller: ResponseEntity.status(409).body(error)
        Controller-->>Tomcat: 409 Conflict + JSON
        Tomcat-->>Browser: HTTP 409<br/>{error: "E-Mail-Adresse ist bereits registriert"}
        Browser->>Browser: Rote Fehlermeldung anzeigen
    else Neue E-Mail ist frei
        Service->>Service: User.setName(neuerName)<br/>User.setEmail(neueEmail)
        Service->>Repo: save(User)
        Repo->>DB: UPDATE users SET name=?, email=? WHERE id=?
        DB-->>Repo: User aktualisiert
        Repo-->>Service: User
        Service->>Service: UserProfileResponse erstellen
        Service-->>Controller: UserProfileResponse
        Controller->>Controller: ResponseEntity.ok(profile)
        Controller-->>Tomcat: 200 OK + JSON
        Tomcat-->>Browser: HTTP 200<br/>{id, name, email, role, createdAt}
        Browser->>Browser: Profil aktualisieren und Erfolgsmeldung anzeigen
    end
```

### 5. Passwort ändern (POST `/api/users/change-password` – geschützt)

```mermaid
sequenceDiagram
    participant Browser as Browser (profile.html)
    participant Tomcat as Tomcat (Spring Boot)
    participant JwtFilter as JwtAuthenticationFilter
    participant Controller as UserController
    participant Service as UserService
    participant BCrypt as BCryptPasswordEncoder
    participant Repo as UserRepository
    participant DB as SQLite (users.db)

    Browser->>Tomcat: POST /api/users/change-password<br/>Authorization: Bearer <JWT-Token><br/>{oldPassword, newPassword, confirmPassword}
    Tomcat->>JwtFilter: doFilterInternal() – Token prüfen
    JwtFilter-->>Tomcat: Token gültig, weiterleiten
    Tomcat->>Controller: changePassword(ChangePasswordRequest)
    Controller->>Service: changePassword(ChangePasswordRequest)
    Service->>Service: Aktuelle email aus SecurityContext
    Service->>Repo: findByEmail(email)
    Repo->>DB: SELECT * FROM users WHERE email=?
    DB-->>Repo: User
    Repo-->>Service: User
    Service->>BCrypt: matches(oldPassword, user.password)
    alt Altes Passwort falsch
        BCrypt-->>Service: false
        Service-->>Controller: InvalidCredentialsException
        Controller->>Controller: ResponseEntity.status(401).body(error)
        Controller-->>Tomcat: 401 Unauthorized + JSON
        Tomcat-->>Browser: HTTP 401<br/>{error: "Altes Passwort ist falsch"}
        Browser->>Browser: Rote Fehlermeldung anzeigen
    else Altes Passwort korrekt
        BCrypt-->>Service: true
        alt Passwörter stimmen nicht überein
            Service-->>Controller: "Passwörter stimmen nicht überein"
            Controller->>Controller: ResponseEntity.status(400).body(error)
            Controller-->>Tomcat: 400 Bad Request + JSON
            Tomcat-->>Browser: HTTP 400<br/>{error: "Passwörter stimmen nicht überein"}
            Browser->>Browser: Rote Fehlermeldung anzeigen
        else Alles gültig
            Service->>BCrypt: encode(newPassword)
            BCrypt-->>Service: Gehashtes Passwort
            Service->>Service: User.setPassword(gehashtesPasswort)
            Service->>Repo: save(User)
            Repo->>DB: UPDATE users SET password=? WHERE id=?
            DB-->>Repo: User aktualisiert
            Repo-->>Service: User
            Service-->>Controller: "Passwort erfolgreich geändert!"
            Controller->>Controller: ResponseEntity.ok(SuccessResponse(message))
            Controller-->>Tomcat: 200 OK + JSON
            Tomcat-->>Browser: HTTP 200<br/>{message: "Passwort erfolgreich geändert!"}
            Browser->>Browser: Grüne Erfolgsmeldung anzeigen
        end
    end
```

---

## Architektur-Überblick

Die Sequenzdiagramme oben veranschaulichen die typische Schichtenarchitektur einer Spring Boot-Anwendung. Jede HTTP-Anfrage durchläuft dabei folgende Komponenten:

| Schicht | Komponente | Verantwortung |
|---------|-----------|---------------|
| **Präsentation** | `UserController` | Empfängt HTTP-Anfragen, validiert DTOs, gibt `ResponseEntity` zurück |
| **Business-Logik** | `UserService` | Enthält die Kernlogik: Validierung, BCrypt-Hashing, JWT-Erstellung |
| **Persistenz** | `UserRepository` | Spring Data JPA-Schnittstelle; generiert SQL-Abfragen automatisch |
| **Sicherheit** | `JwtAuthenticationFilter` + `SecurityConfig` | Prüft JWT-Tokens und schützt Endpunkte |
| **Datenbank** | SQLite (`users.db`) | Speichert Benutzerdaten persistent |

Diese Trennung der Verantwortlichkeiten folgt dem etablierten **Layered Architecture Pattern** und ermöglicht eine klare Struktur, einfache Wartbarkeit und eine gute Testbarkeit jeder Schicht unabhängig voneinander.
