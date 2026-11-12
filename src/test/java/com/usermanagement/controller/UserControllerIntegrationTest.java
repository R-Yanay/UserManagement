package com.usermanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.usermanagement.dto.UserLoginRequest;
import com.usermanagement.dto.UserRegistrationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integrationstests fuer den UserController.
 * Startet die gesamte Anwendung mit einer in-memory SQLite-Testdatenbank,
 * um die API-Endpunkte mit echter Security-Konfiguration zu testen.
 * Jeder Test bekommt eine frische Datenbank, da eine in-memory SQLite verwendet wird.
 */
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // =========================================================================
    //  Registrierungstests
    // =========================================================================

    @Test
    void register_erfolgreich_201Created() throws Exception {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setName("Max Mustermann");
        request.setEmail("test01@test.de");
        request.setPassword("geheim123");

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Max Mustermann"))
                .andExpect(jsonPath("$.email").value("test01@test.de"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    void register_doppelteEmail_409Conflict() throws Exception {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setName("Max Mustermann");
        request.setEmail("test02@test.de");
        request.setPassword("geheim123");

        // Erst registrieren
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Dann nochmal mit derselben E-Mail
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void register_leererName_400BadRequest() throws Exception {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setName("");
        request.setEmail("test03@test.de");
        request.setPassword("geheim123");

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_kurzesPasswort_400BadRequest() throws Exception {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setName("Max");
        request.setEmail("test04@test.de");
        request.setPassword("12345");

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_ungueltigeEmail_400BadRequest() throws Exception {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setName("Max");
        request.setEmail("keine-email");
        request.setPassword("geheim123");

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // =========================================================================
    //  Logintests
    // =========================================================================

    @Test
    void login_erfolgreich_200Ok() throws Exception {
        // Zuerst registrieren
        UserRegistrationRequest regRequest = new UserRegistrationRequest();
        regRequest.setName("Max Mustermann");
        regRequest.setEmail("test05@test.de");
        regRequest.setPassword("geheim123");

        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(regRequest)))
                .andExpect(status().isCreated());

        // Dann einloggen
        UserLoginRequest loginRequest = new UserLoginRequest();
        loginRequest.setEmail("test05@test.de");
        loginRequest.setPassword("geheim123");

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.email").value("test05@test.de"));
    }

    @Test
    void login_falschesPasswort_401Unauthorized() throws Exception {
        // Zuerst registrieren
        UserRegistrationRequest regRequest = new UserRegistrationRequest();
        regRequest.setName("Max Mustermann");
        regRequest.setEmail("test06@test.de");
        regRequest.setPassword("geheim123");

        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(regRequest)))
                .andExpect(status().isCreated());

        // Dann mit falschem Passwort einloggen
        UserLoginRequest loginRequest = new UserLoginRequest();
        loginRequest.setEmail("test06@test.de");
        loginRequest.setPassword("falschesPasswort");

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_nichtRegistriert_401Unauthorized() throws Exception {
        UserLoginRequest loginRequest = new UserLoginRequest();
        loginRequest.setEmail("nicht@registriert.de");
        loginRequest.setPassword("geheim123");

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_leereEmail_400BadRequest() throws Exception {
        UserLoginRequest request = new UserLoginRequest();
        request.setEmail("");
        request.setPassword("geheim123");

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // =========================================================================
    //  Profiltests
    // =========================================================================

    @Test
    void profil_ohneToken_403Forbidden() throws Exception {
        // Ohne Token wird der geschuetzte Endpunkt mit 403 Forbidden abgewiesen
        mockMvc.perform(get("/api/users/profile"))
                .andExpect(status().isForbidden());
    }

    @Test
    void profil_mitGueltigemToken_200Ok() throws Exception {
        // Zuerst registrieren
        UserRegistrationRequest regRequest = new UserRegistrationRequest();
        regRequest.setName("Max Mustermann");
        regRequest.setEmail("test07@test.de");
        regRequest.setPassword("geheim123");

        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(regRequest)))
                .andExpect(status().isCreated());

        // Dann einloggen und Token holen
        UserLoginRequest loginRequest = new UserLoginRequest();
        loginRequest.setEmail("test07@test.de");
        loginRequest.setPassword("geheim123");

        String responseJson = mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // Token aus der JSON-Antwort extrahieren
        String token = objectMapper.readTree(responseJson).get("token").asText();

        // Profil mit Token abrufen
        mockMvc.perform(get("/api/users/profile")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Max Mustermann"))
                .andExpect(jsonPath("$.email").value("test07@test.de"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }
}
