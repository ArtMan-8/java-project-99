package hexlet.code.util;

import hexlet.code.dto.Auth.AuthRequestDTO;
import hexlet.code.dto.Label.LabelCreateDTO;
import hexlet.code.dto.Label.LabelUpdateDTO;
import hexlet.code.dto.Task.TaskCreateDTO;
import hexlet.code.dto.Task.TaskUpdateDTO;
import hexlet.code.dto.TaskStatus.TaskStatusCreateDTO;
import hexlet.code.dto.TaskStatus.TaskStatusUpdateDTO;
import hexlet.code.dto.User.UserCreateDTO;
import hexlet.code.dto.User.UserUpdateDTO;

public class TestDataFactory {

    public static UserCreateDTO createValidUser(String email) {
        UserCreateDTO user = new UserCreateDTO();
        user.setEmail(email);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPassword("password123");
        return user;
    }

    public static TaskCreateDTO createValidTask(String title) {
        TaskCreateDTO task = new TaskCreateDTO();
        task.setIndex(1);
        task.setTitle(title);
        task.setContent("Test content");
        task.setStatus("draft");
        task.setAssigneeId(1L);
        task.setTaskLabelIds(java.util.Set.of(1L, 2L));
        return task;
    }

    public static TaskStatusCreateDTO createValidTaskStatus(String name, String slug) {
        TaskStatusCreateDTO taskStatus = new TaskStatusCreateDTO();
        taskStatus.setName(name);
        taskStatus.setSlug(slug);
        return taskStatus;
    }

    public static LabelCreateDTO createValidLabel(String name) {
        LabelCreateDTO label = new LabelCreateDTO();
        label.setName(name);
        return label;
    }

    public static AuthRequestDTO createAuthRequest(String email, String password) {
        AuthRequestDTO request = new AuthRequestDTO();
        request.setUsername(email);
        request.setPassword(password);
        return request;
    }

    public static TaskUpdateDTO createValidTaskUpdate(String title, String content, String status) {
        TaskUpdateDTO task = new TaskUpdateDTO();
        task.setTitle(title);
        task.setContent(content);
        task.setStatus(status);
        task.setTaskLabelIds(java.util.Set.of(1L));
        return task;
    }

    public static LabelUpdateDTO createValidLabelUpdate(String name) {
        LabelUpdateDTO label = new LabelUpdateDTO();
        label.setName(name);
        return label;
    }

    public static TaskStatusUpdateDTO createValidTaskStatusUpdate(String name, String slug) {
        TaskStatusUpdateDTO taskStatus = new TaskStatusUpdateDTO();
        taskStatus.setName(name);
        taskStatus.setSlug(slug);
        return taskStatus;
    }

    public static UserUpdateDTO createValidUserUpdate(String firstName, String lastName) {
        UserUpdateDTO user = new UserUpdateDTO();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        return user;
    }
}
