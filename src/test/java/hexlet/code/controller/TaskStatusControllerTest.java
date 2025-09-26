package hexlet.code.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.TaskStatus.TaskStatusCreateDTO;
import hexlet.code.dto.TaskStatus.TaskStatusUpdateDTO;
import hexlet.code.model.TaskStatus;
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
class TaskStatusControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestDataFactory testDataFactory;

    private TaskStatus testStatus;

    @BeforeEach
    void setUp() {
        testDataFactory.cleanAll();

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(springSecurity())
                .build();

        testStatus = testDataFactory.createTaskStatus();
    }

    @AfterEach
    void cleanUp() {
        testDataFactory.cleanAll();
    }

    private TaskStatusCreateDTO createValidTaskStatus() {
        TaskStatusCreateDTO taskStatus = new TaskStatusCreateDTO();
        taskStatus.setName("Новый статус");
        taskStatus.setSlug("new_status");
        return taskStatus;
    }

    @Test
    void shouldCreateTaskStatusSuccessfully() throws Exception {
        TaskStatusCreateDTO taskStatus = createValidTaskStatus();
        var result = mockMvc.perform(post("/api/task_statuses")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskStatus)))
                .andExpect(status().isCreated())
                .andReturn();
        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("name").isEqualTo("Новый статус"),
                v -> v.node("slug").isEqualTo("new_status")
        );
    }

    @Test
    void shouldGetAllTaskStatuses() throws Exception {
        var result = mockMvc.perform(get("/api/task_statuses").with(jwt()))
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();

        assertThatJson(body).isArray();
    }

    @Test
    void shouldGetTaskStatusById() throws Exception {
        var result = mockMvc.perform(get("/api/task_statuses/{id}", testStatus.getId()).with(jwt()))
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("name").isEqualTo(testStatus.getName()),
                v -> v.node("slug").isEqualTo(testStatus.getSlug()),
                v -> v.node("id").isEqualTo(testStatus.getId())
        );
    }

    @Test
    void shouldUpdateTaskStatusSuccessfully() throws Exception {
        TaskStatusUpdateDTO taskStatus = new TaskStatusUpdateDTO();
        taskStatus.setName("Обновленный статус");
        taskStatus.setSlug("updated_status");

        var result = mockMvc.perform(put("/api/task_statuses/{id}", testStatus.getId())
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskStatus)))
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("name").isEqualTo("Обновленный статус"),
                v -> v.node("slug").isEqualTo("updated_status")
        );
    }

    @Test
    void shouldDeleteTaskStatusSuccessfully() throws Exception {
        mockMvc.perform(delete("/api/task_statuses/{id}", testStatus.getId()).with(jwt()))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn400WhenCreatingTaskStatusWithInvalidData() throws Exception {
        TaskStatusCreateDTO taskStatus = createValidTaskStatus();
        taskStatus.setName("");

        mockMvc.perform(post("/api/task_statuses")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskStatus)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenUpdatingTaskStatusWithInvalidData() throws Exception {
        TaskStatusUpdateDTO taskStatus = new TaskStatusUpdateDTO();
        taskStatus.setName("");

        mockMvc.perform(put("/api/task_statuses/{id}", testStatus.getId())
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskStatus)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn401WhenAccessingWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/task_statuses")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/task_statuses/{id}", testStatus.getId())).andExpect(status().isUnauthorized());
        mockMvc.perform(post("/api/task_statuses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createValidTaskStatus())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn404ForNonExistentTaskStatus() throws Exception {
        mockMvc.perform(get("/api/task_statuses/999").with(jwt()))
                .andExpect(status().isNotFound());

        TaskStatusUpdateDTO taskStatus = new TaskStatusUpdateDTO();
        taskStatus.setName("Updated Status");

        mockMvc.perform(put("/api/task_statuses/999")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskStatus)))
                .andExpect(status().isNotFound());

        mockMvc.perform(delete("/api/task_statuses/999").with(jwt()))
                .andExpect(status().isNotFound());
    }
}
