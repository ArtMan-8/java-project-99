package hexlet.code.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import hexlet.code.dto.Task.TaskCreateDTO;
import hexlet.code.dto.Task.TaskUpdateDTO;

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
class TaskControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private TaskCreateDTO createValidTask() {
        TaskCreateDTO task = new TaskCreateDTO();
        task.setIndex(1);
        task.setTitle("Test Task");
        task.setContent("Test content");
        task.setStatus("draft");
        task.setAssigneeId(1L);
        task.setLabelIds(java.util.Set.of(1L, 2L));
        return task;
    }

    @Test
    void shouldCreateTaskSuccessfully() throws Exception {
        TaskCreateDTO task = createValidTask();
        mockMvc.perform(post("/api/tasks")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test Task"))
                .andExpect(jsonPath("$.content").value("Test content"))
                .andExpect(jsonPath("$.status").value("draft"))
                .andExpect(jsonPath("$.assigneeId").value(1))
                .andExpect(jsonPath("$.index").value(1))
                .andExpect(jsonPath("$.labelIds").isArray())
                .andExpect(jsonPath("$.labelIds.length()").value(2));
    }

    @Test
    void shouldGetAllTasks() throws Exception {
        mockMvc.perform(get("/api/tasks").with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void shouldGetTaskById() throws Exception {
        TaskCreateDTO task = createValidTask();
        mockMvc.perform(post("/api/tasks")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Test Task"))
                .andExpect(jsonPath("$.content").value("Test content"))
                .andExpect(jsonPath("$.status").value("draft"));
    }

    @Test
    void shouldUpdateTaskSuccessfully() throws Exception {
        TaskUpdateDTO updateTask = new TaskUpdateDTO();
        updateTask.setTitle("Updated Task");
        updateTask.setContent("Updated content");
        updateTask.setStatus("to_review");
        updateTask.setLabelIds(java.util.Set.of(1L));

        mockMvc.perform(put("/api/tasks/1")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateTask)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Task"))
                .andExpect(jsonPath("$.content").value("Updated content"))
                .andExpect(jsonPath("$.status").value("to_review"))
                .andExpect(jsonPath("$.labelIds").isArray())
                .andExpect(jsonPath("$.labelIds.length()").value(1));
    }

    @Test
    void shouldDeleteTaskSuccessfully() throws Exception {
        mockMvc.perform(delete("/api/tasks/1").with(jwt()))
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

        mockMvc.perform(put("/api/tasks/1")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateTask)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn401WhenAccessingWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/tasks")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/tasks/1")).andExpect(status().isUnauthorized());

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
    void shouldGetFilteredTasks() throws Exception {
        mockMvc.perform(get("/api/tasks?titleCont=Test").with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        mockMvc.perform(get("/api/tasks?assigneeId=1").with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        mockMvc.perform(get("/api/tasks?status=draft").with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        mockMvc.perform(get("/api/tasks?labelId=1").with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        mockMvc.perform(get("/api/tasks?titleCont=Test&assigneeId=1&status=draft&labelId=1").with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
