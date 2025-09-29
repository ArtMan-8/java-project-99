package hexlet.code.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import hexlet.code.dto.User.UserCreateDTO;
import hexlet.code.dto.User.UserUpdateDTO;
import hexlet.code.dto.Task.TaskCreateDTO;
import hexlet.code.util.TestDataFactory;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateAndGetUserById() throws Exception {
        UserCreateDTO user = TestDataFactory.createValidUser("test@example.com");
        String createResponse = mockMvc.perform(post("/api/users")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThatJson(createResponse)
                .isObject()
                .containsEntry("email", "test@example.com")
                .containsEntry("firstName", "John")
                .containsEntry("lastName", "Doe");

        Long userId = objectMapper.readTree(createResponse).get("id").asLong();

        String getResponse = mockMvc.perform(get("/api/users/" + userId)
                .with(jwt()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThatJson(getResponse)
                .isObject()
                .containsEntry("email", "test@example.com");
    }

    @Test
    void shouldGetAllUsers() throws Exception {
        mockMvc.perform(get("/api/users")
                .with(jwt()))
                .andExpect(status().isOk());
    }

    @Test
    void shouldUpdateUserSuccessfully() throws Exception {
        UserCreateDTO user = TestDataFactory.createValidUser("testuser@example.com");
        String createResponse = mockMvc.perform(post("/api/users")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long userId = objectMapper.readTree(createResponse).get("id").asLong();

        UserUpdateDTO updateUser = TestDataFactory.createValidUserUpdate("Jane", "Smith");

        String response = mockMvc.perform(put("/api/users/" + userId)
                .with(jwt().jwt(jwt -> jwt.subject("testuser@example.com")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateUser)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThatJson(response)
                .isObject()
                .containsEntry("firstName", "Jane")
                .containsEntry("lastName", "Smith");
    }

    @Test
    void shouldDeleteUserSuccessfully() throws Exception {
        UserCreateDTO user = TestDataFactory.createValidUser("newuser@example.com");

        String response = mockMvc.perform(post("/api/users")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(delete("/api/users/" + id)
                .with(jwt().jwt(jwt -> jwt.subject("newuser@example.com"))))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn400WhenCreatingUserWithInvalidData() throws Exception {
        UserCreateDTO user = TestDataFactory.createValidUser("test@example.com");
        user.setEmail("invalid-email");

        mockMvc.perform(post("/api/users")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenUpdatingUserWithInvalidData() throws Exception {
        UserCreateDTO user = TestDataFactory.createValidUser("testuser@example.com");
        String createResponse = mockMvc.perform(post("/api/users")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long userId = objectMapper.readTree(createResponse).get("id").asLong();

        UserUpdateDTO updateUser = TestDataFactory.createValidUserUpdate("Jane", "Smith");
        updateUser.setEmail("invalid-email");

        mockMvc.perform(put("/api/users/" + userId)
                .with(jwt().jwt(jwt -> jwt.subject("testuser@example.com")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn401WhenAccessingWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(TestDataFactory.createValidUser("test@example.com"))))
                .andExpect(status().isUnauthorized());

        UserUpdateDTO updateUser = TestDataFactory.createValidUserUpdate("Jane", "Smith");
        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateUser)))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn404ForNonExistentUser() throws Exception {
        mockMvc.perform(get("/api/users/999")
                .with(jwt()))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn400WhenDeletingUserWithTasks() throws Exception {
        UserCreateDTO user = TestDataFactory.createValidUser("testuser@example.com");
        String userResponse = mockMvc.perform(post("/api/users")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long userId = objectMapper.readTree(userResponse).get("id").asLong();

        TaskCreateDTO task = TestDataFactory.createValidTask("Test Task");
        task.setAssigneeId(userId);
        mockMvc.perform(post("/api/tasks")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isCreated());

        mockMvc.perform(delete("/api/users/" + userId)
                .with(jwt().jwt(jwt -> jwt.subject("testuser@example.com"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenCreatingUserWithDuplicateEmail() throws Exception {
        UserCreateDTO user = TestDataFactory.createValidUser("test@example.com");
        user.setEmail("hexlet@example.com");

        mockMvc.perform(post("/api/users")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isBadRequest());
    }
}
