package hexlet.code.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import hexlet.code.dto.Task.TaskCreateDTO;
import hexlet.code.dto.Task.TaskUpdateDTO;
import hexlet.code.util.TestDataFactory;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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
class TaskControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateAndGetTaskById() throws Exception {
        TaskCreateDTO task = TestDataFactory.createValidTask("Test Task");
        String createResponse = mockMvc.perform(post("/api/tasks")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThatJson(createResponse)
                .isObject()
                .containsEntry("title", "Test Task")
                .containsEntry("content", "Test content")
                .containsEntry("status", "draft")
                .containsEntry("assignee_id", 1)
                .containsEntry("index", 1)
                .node("taskLabelIds").isArray().hasSize(2);

        Long taskId = objectMapper.readTree(createResponse).get("id").asLong();

        String getResponse = mockMvc.perform(get("/api/tasks/" + taskId)
                .with(jwt()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThatJson(getResponse)
                .isObject()
                .containsEntry("title", "Test Task")
                .containsEntry("content", "Test content")
                .containsEntry("status", "draft");
    }

    @Test
    void shouldGetAllTasks() throws Exception {
        mockMvc.perform(get("/api/tasks")
                .with(jwt()))
                .andExpect(status().isOk());
    }

    @Test
    void shouldUpdateTaskSuccessfully() throws Exception {
        TaskCreateDTO task = TestDataFactory.createValidTask("Test Task");
        String response = mockMvc.perform(post("/api/tasks")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long taskId = objectMapper.readTree(response).get("id").asLong();

        TaskUpdateDTO updateTask = TestDataFactory.createValidTaskUpdate(
                "Updated Task", "Updated content", "to_review");

        String updateResponse = mockMvc.perform(put("/api/tasks/" + taskId)
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateTask)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThatJson(updateResponse)
                .isObject()
                .containsEntry("title", "Updated Task")
                .containsEntry("content", "Updated content")
                .containsEntry("status", "to_review")
                .node("taskLabelIds").isArray().hasSize(1);
    }

    @Test
    void shouldDeleteTaskSuccessfully() throws Exception {
        TaskCreateDTO task = TestDataFactory.createValidTask("Test Task");
        String response = mockMvc.perform(post("/api/tasks")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long taskId = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(delete("/api/tasks/" + taskId)
                .with(jwt()))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn400WhenCreatingTaskWithInvalidData() throws Exception {
        TaskCreateDTO task = TestDataFactory.createValidTask("Test Task");
        task.setTitle("");

        mockMvc.perform(post("/api/tasks")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenUpdatingTaskWithInvalidData() throws Exception {
        TaskCreateDTO task = TestDataFactory.createValidTask("Test Task");
        String createResponse = mockMvc.perform(post("/api/tasks")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long taskId = objectMapper.readTree(createResponse).get("id").asLong();

        TaskUpdateDTO updateTask = TestDataFactory.createValidTaskUpdate(
                "Updated Task", "Updated content", "to_review");
        updateTask.setTitle("");

        mockMvc.perform(put("/api/tasks/" + taskId)
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateTask)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn401WhenAccessingWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isUnauthorized());

        TaskCreateDTO task = TestDataFactory.createValidTask("Test Task");
        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isUnauthorized());

        TaskUpdateDTO updateTask = new TaskUpdateDTO();
        updateTask.setTitle("Updated Task");
        mockMvc.perform(put("/api/tasks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateTask)))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(delete("/api/tasks/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn404ForNonExistentTask() throws Exception {
        mockMvc.perform(get("/api/tasks/999")
                .with(jwt()))
                .andExpect(status().isNotFound());

        TaskUpdateDTO updateTask = new TaskUpdateDTO();
        updateTask.setTitle("Updated Task");

        mockMvc.perform(put("/api/tasks/999")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateTask)))
                .andExpect(status().isNotFound());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "titleCont=Test",
        "status=draft",
        "assigneeId=1",
        "titleCont=Test&status=draft"
    })
    void shouldGetFilteredTasks(String filter) throws Exception {
        mockMvc.perform(get("/api/tasks?" + filter)
                .with(jwt()))
                .andExpect(status().isOk());
    }

    @Test
    void shouldCreateTaskWithLabels() throws Exception {
        TaskCreateDTO task = TestDataFactory.createValidTask("Task with Labels");
        task.setContent("Task description with labels");
        task.setTaskLabelIds(java.util.Set.of(1L, 2L, 3L));

        String response = mockMvc.perform(post("/api/tasks")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThatJson(response)
                .isObject()
                .containsEntry("title", "Task with Labels")
                .containsEntry("content", "Task description with labels")
                .node("taskLabelIds").isArray().hasSize(3);

        assertThatJson(response)
                .node("taskLabelIds").isArray()
                .contains(1, 2, 3);
    }

    @Test
    void shouldUpdateTaskLabels() throws Exception {
        TaskCreateDTO task = TestDataFactory.createValidTask("Task for Label Update");
        task.setTaskLabelIds(java.util.Set.of(1L, 2L));

        String response = mockMvc.perform(post("/api/tasks")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long taskId = objectMapper.readTree(response).get("id").asLong();

        TaskUpdateDTO updateTask = new TaskUpdateDTO();
        updateTask.setTaskLabelIds(java.util.Set.of(2L, 3L, 4L));

        String updateResponse = mockMvc.perform(put("/api/tasks/" + taskId)
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateTask)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThatJson(updateResponse)
                .isObject()
                .node("taskLabelIds").isArray().hasSize(3);

        assertThatJson(updateResponse)
                .node("taskLabelIds").isArray()
                .contains(2, 3, 4);
    }
}
