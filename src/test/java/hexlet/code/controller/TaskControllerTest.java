package hexlet.code.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.Task.TaskCreateDTO;
import hexlet.code.dto.Task.TaskUpdateDTO;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
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
import java.util.Set;

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
class TaskControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestDataFactory testDataFactory;

    private Task testTask;
    private User testUser;
    private TaskStatus testStatus;
    private Label testLabel;

    @BeforeEach
    void setUp() {
        testDataFactory.cleanAll();

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(springSecurity())
                .build();

        testUser = testDataFactory.createUser();
        testStatus = testDataFactory.createTaskStatus();
        testLabel = testDataFactory.createLabel();
        testTask = testDataFactory.createTask(testUser, testStatus, Set.of(testLabel));
    }

    @AfterEach
    void cleanUp() {
        testDataFactory.cleanAll();
    }

    private TaskCreateDTO createValidTask() {
        TaskCreateDTO task = new TaskCreateDTO();
        task.setIndex(1);
        task.setTitle("Test Task");
        task.setContent("Test content");
        task.setStatus(testStatus.getSlug());
        task.setAssigneeId(testUser.getId());
        task.setTaskLabelIds(new java.util.HashSet<>(Set.of(testLabel.getId())));
        return task;
    }

    @Test
    @Transactional
    void shouldCreateTaskSuccessfully() throws Exception {
        TaskCreateDTO task = createValidTask();
        var result = mockMvc.perform(post("/api/tasks")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isCreated())
                .andReturn();
        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("id").isPresent(),
                v -> v.node("title").isEqualTo("Test Task"),
                v -> v.node("content").isEqualTo("Test content"),
                v -> v.node("status").isEqualTo(testStatus.getSlug()),
                v -> v.node("assignee_id").isEqualTo(testUser.getId()),
                v -> v.node("index").isEqualTo(1),
                v -> v.node("taskLabelIds").isArray(),
                v -> v.node("createdAt").isPresent()
        );
    }

    @Test
    void shouldGetAllTasks() throws Exception {
        var result = mockMvc.perform(get("/api/tasks").with(jwt()))
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();

        assertThatJson(body).isArray();
    }

    @Test
    void shouldGetTaskById() throws Exception {
        var result = mockMvc.perform(get("/api/tasks/{id}", testTask.getId()).with(jwt()))
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("title").isEqualTo(testTask.getTitle()),
                v -> v.node("content").isEqualTo(testTask.getContent()),
                v -> v.node("assignee_id").isEqualTo(testTask.getAssignee().getId()),
                v -> v.node("status").isEqualTo(testTask.getTaskStatus().getSlug())
        );
    }

    @Test
    void shouldUpdateTaskSuccessfully() throws Exception {
        TaskUpdateDTO updateTask = new TaskUpdateDTO();
        updateTask.setTitle("Updated Task");
        updateTask.setContent("Updated content");
        updateTask.setStatus(testStatus.getSlug());
        updateTask.setTaskLabelIds(new java.util.HashSet<>(Set.of(testLabel.getId())));

        var result = mockMvc.perform(put("/api/tasks/{id}", testTask.getId())
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateTask)))
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("title").isEqualTo("Updated Task"),
                v -> v.node("content").isEqualTo("Updated content"),
                v -> v.node("status").isEqualTo(testStatus.getSlug()),
                v -> v.node("taskLabelIds").isArray()
        );
    }

    @Test
    void shouldHandlePartialUpdate() throws Exception {
        TaskUpdateDTO updateTask = new TaskUpdateDTO();
        updateTask.setTitle("New Task Name");
        updateTask.setStatus(testStatus.getSlug());

        var result = mockMvc.perform(put("/api/tasks/{id}", testTask.getId())
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateTask)))
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();

        assertThatJson(body).and(
                v -> v.node("title").isEqualTo("New Task Name")
        );
    }

    @Test
    void shouldDeleteTaskSuccessfully() throws Exception {
        mockMvc.perform(delete("/api/tasks/{id}", testTask.getId()).with(jwt()))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn400WhenCreatingTaskWithInvalidData() throws Exception {
        TaskCreateDTO task = createValidTask();
        task.setTitle("");

        mockMvc.perform(post("/api/tasks")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenUpdatingTaskWithInvalidData() throws Exception {
        TaskUpdateDTO updateTask = new TaskUpdateDTO();
        updateTask.setTitle("");

        mockMvc.perform(put("/api/tasks/{id}", testTask.getId())
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateTask)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn401WhenAccessingWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/tasks")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/tasks/{id}", testTask.getId())).andExpect(status().isUnauthorized());

        TaskCreateDTO task = createValidTask();
        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn404ForNonExistentTask() throws Exception {
        mockMvc.perform(get("/api/tasks/999").with(jwt()))
                .andExpect(status().isNotFound());

        TaskUpdateDTO updateTask = new TaskUpdateDTO();
        updateTask.setTitle("Updated Task");

        mockMvc.perform(put("/api/tasks/999")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateTask)))
                .andExpect(status().isNotFound());

        mockMvc.perform(delete("/api/tasks/999").with(jwt()))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetFilteredTasksByTitle() throws Exception {
        String searchTerm = testTask.getTitle().length() > 3
                ? testTask.getTitle().substring(0, 3)
                : testTask.getTitle();
        var result = mockMvc.perform(get("/api/tasks?titleCont=" + searchTerm).with(jwt()))
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();

        assertThatJson(body).isArray().allSatisfy(element ->
                assertThatJson(element)
                        .and(v -> v.node("title").asString().containsIgnoringCase(testTask.getTitle()))
        );
    }

    @Test
    void shouldGetFilteredTasksByAssignee() throws Exception {
        var result = mockMvc.perform(get("/api/tasks?assigneeId=" + testUser.getId()).with(jwt()))
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();

        assertThatJson(body).isArray().allSatisfy(element ->
                assertThatJson(element)
                        .and(v -> v.node("assignee_id").isEqualTo(testUser.getId()))
        );
    }

    @Test
    void shouldGetFilteredTasksByStatus() throws Exception {
        var result = mockMvc.perform(get("/api/tasks?status=" + testStatus.getSlug()).with(jwt()))
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();

        assertThatJson(body).isArray().allSatisfy(element ->
                assertThatJson(element)
                        .and(v -> v.node("status").isEqualTo(testStatus.getSlug()))
        );
    }

    @Test
    void shouldGetFilteredTasksByLabel() throws Exception {
        var result = mockMvc.perform(get("/api/tasks?labelId=" + testLabel.getId()).with(jwt()))
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();

        assertThatJson(body).isArray().allSatisfy(element ->
                assertThatJson(element)
                        .and(v -> v.node("taskLabelIds").isArray())
        );
    }

    @Test
    void shouldGetFilteredTasksWithMultipleParams() throws Exception {
        String searchTerm = testTask.getTitle().length() > 3
                ? testTask.getTitle().substring(0, 3)
                : testTask.getTitle();
        var result = mockMvc.perform(get("/api/tasks?titleCont=" + searchTerm
                        + "&assigneeId=" + testUser.getId()
                        + "&status=" + testStatus.getSlug()
                        + "&labelId=" + testLabel.getId()).with(jwt()))
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();

        assertThatJson(body).isArray().allSatisfy(element ->
                assertThatJson(element)
                        .and(v -> v.node("title").isEqualTo(testTask.getTitle()))
                        .and(v -> v.node("status").isEqualTo(testStatus.getSlug()))
                        .and(v -> v.node("assignee_id").isEqualTo(testUser.getId()))
        );
    }
}
