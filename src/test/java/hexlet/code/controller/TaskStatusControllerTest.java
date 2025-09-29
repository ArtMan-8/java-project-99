package hexlet.code.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import hexlet.code.dto.TaskStatus.TaskStatusCreateDTO;
import hexlet.code.dto.TaskStatus.TaskStatusUpdateDTO;
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
class TaskStatusControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void shouldCreateTaskStatusSuccessfully() throws Exception {
        TaskStatusCreateDTO taskStatus = TestDataFactory.createValidTaskStatus("Новый статус", "new_status");
        String response = mockMvc.perform(post("/api/task_statuses")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskStatus)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThatJson(response)
                .isObject()
                .containsEntry("name", "Новый статус")
                .containsEntry("slug", "new_status");
    }

    @Test
    void shouldGetAllTaskStatuses() throws Exception {
        mockMvc.perform(get("/api/task_statuses")
                .with(jwt()))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetTaskStatusById() throws Exception {
        String response = mockMvc.perform(get("/api/task_statuses/1")
                .with(jwt()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThatJson(response)
                .isObject()
                .containsEntry("name", "Черновик")
                .containsEntry("slug", "draft");
    }

    @Test
    void shouldUpdateTaskStatusSuccessfully() throws Exception {
        TaskStatusUpdateDTO taskStatus = new TaskStatusUpdateDTO();
        taskStatus.setName("Обновленный статус");
        taskStatus.setSlug("updated_status");

        String response = mockMvc.perform(put("/api/task_statuses/1")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskStatus)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThatJson(response)
                .isObject()
                .containsEntry("name", "Обновленный статус")
                .containsEntry("slug", "updated_status");
    }

    @Test
    void shouldDeleteTaskStatusSuccessfully() throws Exception {
        TaskStatusCreateDTO taskStatus = TestDataFactory.createValidTaskStatus("Новый статус", "new_status");
        taskStatus.setName("Новый статус для удаления");
        taskStatus.setSlug("new_status_for_deletion");

        String response = mockMvc.perform(post("/api/task_statuses")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskStatus)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long id = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(delete("/api/task_statuses/" + id)
                .with(jwt()))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn400WhenCreatingTaskStatusWithInvalidName() throws Exception {
        TaskStatusCreateDTO taskStatus = TestDataFactory.createValidTaskStatus("Новый статус", "new_status");
        taskStatus.setName("");

        mockMvc.perform(post("/api/task_statuses")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskStatus)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenUpdatingTaskStatusWithInvalidName() throws Exception {
        TaskStatusCreateDTO taskStatus = TestDataFactory.createValidTaskStatus("Новый статус", "new_status");
        String createResponse = mockMvc.perform(post("/api/task_statuses")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskStatus)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long statusId = objectMapper.readTree(createResponse).get("id").asLong();

        TaskStatusUpdateDTO updateTaskStatus = TestDataFactory.createValidTaskStatusUpdate(
                "Обновленный статус", "updated_status");
        updateTaskStatus.setName("");

        mockMvc.perform(put("/api/task_statuses/" + statusId)
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateTaskStatus)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn401WhenAccessingWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/task_statuses"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/task_statuses/1"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/task_statuses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                    TestDataFactory.createValidTaskStatus("Новый статус", "new_status"))))
                .andExpect(status().isUnauthorized());

        TaskStatusUpdateDTO updateTaskStatus = TestDataFactory.createValidTaskStatusUpdate(
                "Обновленный статус", "updated_status");
        mockMvc.perform(put("/api/task_statuses/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateTaskStatus)))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(delete("/api/task_statuses/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn404ForNonExistentTaskStatus() throws Exception {
        mockMvc.perform(get("/api/task_statuses/999")
                .with(jwt()))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn400WhenDeletingTaskStatusWithTasks() throws Exception {
        TaskStatusCreateDTO taskStatus = TestDataFactory.createValidTaskStatus(
                "Test Status for Deletion", "test_status_for_deletion");

        String statusResponse = mockMvc.perform(post("/api/task_statuses")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskStatus)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long statusId = objectMapper.readTree(statusResponse).get("id").asLong();

        TaskCreateDTO task = TestDataFactory.createValidTask("Test Task with Status");
        task.setStatus("test_status_for_deletion");

        mockMvc.perform(post("/api/tasks")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isCreated());

        mockMvc.perform(delete("/api/task_statuses/" + statusId)
                .with(jwt()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenCreatingTaskStatusWithDuplicateName() throws Exception {
        TaskStatusCreateDTO taskStatus = TestDataFactory.createValidTaskStatus("Новый статус", "new_status");
        taskStatus.setName("Draft");
        taskStatus.setSlug("duplicate_name_test");

        mockMvc.perform(post("/api/task_statuses")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskStatus)))
                .andExpect(status().isCreated());

        TaskStatusCreateDTO duplicateTaskStatus = TestDataFactory.createValidTaskStatus("Новый статус", "new_status");
        duplicateTaskStatus.setName("Draft");
        duplicateTaskStatus.setSlug("another_duplicate_slug");

        mockMvc.perform(post("/api/task_statuses")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateTaskStatus)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenCreatingTaskStatusWithDuplicateSlug() throws Exception {
        TaskStatusCreateDTO taskStatus = TestDataFactory.createValidTaskStatus("Новый статус", "new_status");
        taskStatus.setName("Duplicate Slug Test");
        taskStatus.setSlug("draft");

        mockMvc.perform(post("/api/task_statuses")
                .with(jwt())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskStatus)))
                .andExpect(status().isBadRequest());
    }
}
