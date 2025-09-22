package hexlet.code.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.UserCreateDTO;
import hexlet.code.dto.UserUpdateDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private UserCreateDTO createValidUser() {
        UserCreateDTO user = new UserCreateDTO();
        user.setEmail("test@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPassword("password123");
        return user;
    }

    @Test
    void shouldCreateUserSuccessfully() throws Exception {
        UserCreateDTO user = createValidUser();
        mockMvc.perform(post("/api/users")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    @Test
    void shouldGetAllUsers() throws Exception {
        mockMvc.perform(get("/api/users").with(jwt()))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetUserById() throws Exception {
        mockMvc.perform(get("/api/users/1").with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("hexlet@example.com"));
    }

    @Test
    void shouldUpdateUserSuccessfully() throws Exception {
        UserUpdateDTO user = new UserUpdateDTO();
        user.setFirstName("Jane");
        user.setLastName("Smith");

        mockMvc.perform(put("/api/users/1")
                .with(jwt().jwt(jwt -> jwt.subject("hexlet@example.com")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.lastName").value("Smith"));
    }

    @Test
    void shouldDeleteUserSuccessfully() throws Exception {
        mockMvc.perform(delete("/api/users/1")
                .with(jwt().jwt(jwt -> jwt.subject("hexlet@example.com"))))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn403WhenCreatingUserWithInvalidData() throws Exception {
        UserCreateDTO user = createValidUser();
        user.setEmail("invalid-email");

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturn403WhenUpdatingUserWithInvalidData() throws Exception {
        UserUpdateDTO user = new UserUpdateDTO();
        user.setEmail("invalid-email");

        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturn403WhenAccessingWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/users")).andExpect(status().isForbidden());
        mockMvc.perform(get("/api/users/1")).andExpect(status().isForbidden());
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createValidUser())))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturn404ForNonExistentUser() throws Exception {
        mockMvc.perform(get("/api/users/999").with(jwt()))
                .andExpect(status().isNotFound());
    }
}
