package hexlet.code.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import hexlet.code.dto.TaskStatus.TaskStatusCreateDTO;
import hexlet.code.dto.TaskStatus.TaskStatusUpdateDTO;

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
class TaskStatusControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private TaskStatusCreateDTO createValidTaskStatus() {
        TaskStatusCreateDTO taskStatus = new TaskStatusCreateDTO();
        taskStatus.setName("Новый статус");
        taskStatus.setSlug("new_status");
        return taskStatus;
    }

    @Test
    void shouldCreateTaskStatusSuccessfully() throws Exception {
        TaskStatusCreateDTO taskStatus = createValidTaskStatus();
        mockMvc.perform(post("/api/task_statuses")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskStatus)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Новый статус"))
                .andExpect(jsonPath("$.slug").value("new_status"));
    }

    @Test
    void shouldGetAllTaskStatuses() throws Exception {
        mockMvc.perform(get("/api/task_statuses").with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Черновик"))
                .andExpect(jsonPath("$[0].slug").value("draft"));
    }

    @Test
    void shouldGetTaskStatusById() throws Exception {
        mockMvc.perform(get("/api/task_statuses/1").with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Черновик"))
                .andExpect(jsonPath("$.slug").value("draft"));
    }

    @Test
    void shouldUpdateTaskStatusSuccessfully() throws Exception {
        TaskStatusUpdateDTO taskStatus = new TaskStatusUpdateDTO();
        taskStatus.setName("Обновленный статус");
        taskStatus.setSlug("updated_status");

        mockMvc.perform(put("/api/task_statuses/1")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskStatus)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Обновленный статус"))
                .andExpect(jsonPath("$.slug").value("updated_status"));
    }

    @Test
    void shouldDeleteTaskStatusSuccessfully() throws Exception {
        mockMvc.perform(delete("/api/task_statuses/1").with(jwt()))
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

        mockMvc.perform(put("/api/task_statuses/1")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskStatus)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn401WhenAccessingWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/task_statuses")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/task_statuses/1")).andExpect(status().isUnauthorized());
        mockMvc.perform(post("/api/task_statuses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createValidTaskStatus())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn404ForNonExistentTaskStatus() throws Exception {
        mockMvc.perform(get("/api/task_statuses/999").with(jwt()))
                .andExpect(status().isNotFound());
    }
}
