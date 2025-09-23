package hexlet.code.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import hexlet.code.dto.Auth.AuthRequestDTO;
import hexlet.code.dto.User.UserCreateDTO;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private AuthRequestDTO createAuthRequest(String email, String password) {
        AuthRequestDTO request = new AuthRequestDTO();
        request.setUsername(email);
        request.setPassword(password);
        return request;
    }

    private UserCreateDTO createUser() {
        UserCreateDTO user = new UserCreateDTO();
        user.setEmail("test@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPassword("password123");
        return user;
    }

    @Test
    void shouldLoginSuccessfully() throws Exception {
        mockMvc.perform(post("/api/users")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createUser())))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createAuthRequest("test@example.com", "password123"))))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturn400WithInvalidData() throws Exception {
        mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createAuthRequest("invalid-email", "password123"))))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createAuthRequest("", "password123"))))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createAuthRequest("test@example.com", ""))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn401WithInvalidCredentials() throws Exception {
        mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    createAuthRequest("nonexistent@example.com", "wrongpassword")
                )))
                .andExpect(status().isUnauthorized());
    }
}
