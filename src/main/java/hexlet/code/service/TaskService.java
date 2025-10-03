package hexlet.code.service;

import hexlet.code.dto.Task.TaskCreateDTO;
import hexlet.code.dto.Task.TaskFilterDTO;
import hexlet.code.dto.Task.TaskResponseDTO;
import hexlet.code.dto.Task.TaskUpdateDTO;

import java.util.List;

public interface TaskService {
    List<TaskResponseDTO> getAllTasks(TaskFilterDTO filter);
    TaskResponseDTO getTaskById(Long id);
    TaskResponseDTO createTask(TaskCreateDTO taskCreateDTO);
    TaskResponseDTO updateTask(Long id, TaskUpdateDTO taskUpdateDTO);
    void deleteTask(Long id);
}
