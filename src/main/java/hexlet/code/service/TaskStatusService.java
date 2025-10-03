package hexlet.code.service;

import hexlet.code.dto.TaskStatus.TaskStatusCreateDTO;
import hexlet.code.dto.TaskStatus.TaskStatusResponseDTO;
import hexlet.code.dto.TaskStatus.TaskStatusUpdateDTO;

import java.util.List;

public interface TaskStatusService {
    List<TaskStatusResponseDTO> getAllTaskStatuses();
    TaskStatusResponseDTO getTaskStatusById(Long id);
    TaskStatusResponseDTO createTaskStatus(TaskStatusCreateDTO taskStatusCreateDTO);
    TaskStatusResponseDTO updateTaskStatus(Long id, TaskStatusUpdateDTO taskStatusUpdateDTO);
    void deleteTaskStatus(Long id);
}
