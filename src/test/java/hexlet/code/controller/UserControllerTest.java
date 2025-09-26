package hexlet.code.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.User.UserCreateDTO;
import hexlet.code.dto.User.UserUpdateDTO;
import hexlet.code.model.User;
import hexlet.code.utils.TestDataFactory;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestDataFactory testDataFactory;

    private User testUser;

    @BeforeEach
    void setUp() {
        testDataFactory.cleanAll();

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(springSecurity())
                .build();

        testUser = testDataFactory.createUser();
    }

    @AfterEach
    void cleanUp() {
        testDataFactory.cleanAll();
    }

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
        var result = mockMvc.perform(post("/api/users")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andReturn();
        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("email").isEqualTo("test@example.com"),
                v -> v.node("firstName").isEqualTo("John"),
                v -> v.node("lastName").isEqualTo("Doe"),
                v -> v.node("id").isPresent(),
                v -> v.node("createdAt").isPresent(),
                v -> v.node("password").isAbsent()
        );
    }

    @Test
    void shouldGetAllUsers() throws Exception {
        var result = mockMvc.perform(get("/api/users").with(jwt()))
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();

        assertThatJson(body).isArray();
    }

    @Test
    void shouldGetUserById() throws Exception {
        var result = mockMvc.perform(get("/api/users/{id}", testUser.getId()).with(jwt()))
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("firstName").isEqualTo(testUser.getFirstName()),
                v -> v.node("lastName").isEqualTo(testUser.getLastName()),
                v -> v.node("email").isEqualTo(testUser.getEmail()),
                v -> v.node("id").isEqualTo(testUser.getId())
        );
    }

    @Test
    void shouldUpdateUserSuccessfully() throws Exception {
        UserUpdateDTO user = new UserUpdateDTO();
        user.setFirstName("Jane");
        user.setLastName("Smith");

        var result = mockMvc.perform(put("/api/users/{id}", testUser.getId())
                .with(jwt().jwt(jwt -> jwt.subject(testUser.getEmail())))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("firstName").isEqualTo("Jane"),
                v -> v.node("lastName").isEqualTo("Smith"),
                v -> v.node("password").isAbsent()
        );
    }

    @Test
    void shouldHandlePartialUpdate() throws Exception {
        UserUpdateDTO user = new UserUpdateDTO();
        user.setFirstName("New name");

        var result = mockMvc.perform(put("/api/users/{id}", testUser.getId())
                .with(jwt().jwt(jwt -> jwt.subject(testUser.getEmail())))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("firstName").isEqualTo("New name"),
                v -> v.node("password").isAbsent()
        );
    }

    @Test
    void shouldDeleteUserSuccessfully() throws Exception {
        mockMvc.perform(delete("/api/users/{id}", testUser.getId())
                .with(jwt().jwt(jwt -> jwt.subject(testUser.getEmail()))))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn400WhenCreatingUserWithInvalidData() throws Exception {
        UserCreateDTO user = createValidUser();
        user.setEmail("invalid-email");

        mockMvc.perform(post("/api/users")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenUpdatingUserWithInvalidData() throws Exception {
        UserUpdateDTO user = new UserUpdateDTO();
        user.setEmail("invalid-email");

        mockMvc.perform(put("/api/users/{id}", testUser.getId())
                .with(jwt().jwt(jwt -> jwt.subject(testUser.getEmail())))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn401WhenAccessingWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/users")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/users/{id}", testUser.getId())).andExpect(status().isUnauthorized());

        UserCreateDTO user = createValidUser();
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn404ForNonExistentUser() throws Exception {
        mockMvc.perform(get("/api/users/999").with(jwt()))
                .andExpect(status().isNotFound());

        UserUpdateDTO user = new UserUpdateDTO();
        user.setFirstName("Updated Name");

        mockMvc.perform(put("/api/users/999")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isNotFound());

        mockMvc.perform(delete("/api/users/999").with(jwt()))
                .andExpect(status().isNotFound());
    }
}
