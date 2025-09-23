package hexlet.code.service;

import hexlet.code.dto.TaskStatus.TaskStatusCreateDTO;
import hexlet.code.dto.TaskStatus.TaskStatusResponseDTO;
import hexlet.code.dto.TaskStatus.TaskStatusUpdateDTO;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class TaskStatusService {
    private final TaskStatusRepository taskStatusRepository;

    public List<TaskStatusResponseDTO> getAllTaskStatuses() {
        return taskStatusRepository.findAll().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public long getTotalTaskStatusesCount() {
        return taskStatusRepository.count();
    }

    public Optional<TaskStatusResponseDTO> getTaskStatusById(Long id) {
        return taskStatusRepository.findById(id)
                .map(this::toResponseDTO);
    }

    public TaskStatusResponseDTO createTaskStatus(TaskStatusCreateDTO taskStatusCreateDTO) {
        TaskStatus taskStatus = new TaskStatus();
        taskStatus.setName(taskStatusCreateDTO.getName());
        taskStatus.setSlug(taskStatusCreateDTO.getSlug());

        TaskStatus savedTaskStatus = taskStatusRepository.save(taskStatus);
        return toResponseDTO(savedTaskStatus);
    }

    public Optional<TaskStatusResponseDTO> updateTaskStatus(Long id, TaskStatusUpdateDTO taskStatusUpdateDTO) {
        return taskStatusRepository.findById(id)
                .map(taskStatus -> {
                    if (taskStatusUpdateDTO.getName() != null) {
                        taskStatus.setName(taskStatusUpdateDTO.getName());
                    }

                    if (taskStatusUpdateDTO.getSlug() != null) {
                        taskStatus.setSlug(taskStatusUpdateDTO.getSlug());
                    }

                    TaskStatus savedTaskStatus = taskStatusRepository.save(taskStatus);
                    return toResponseDTO(savedTaskStatus);
                });
    }

    public boolean deleteTaskStatus(Long id) {
        if (taskStatusRepository.existsById(id)) {
            taskStatusRepository.deleteById(id);
            return true;
        }

        return false;
    }

    private TaskStatusResponseDTO toResponseDTO(TaskStatus taskStatus) {
        TaskStatusResponseDTO dto = new TaskStatusResponseDTO();
        dto.setId(taskStatus.getId());
        dto.setName(taskStatus.getName());
        dto.setSlug(taskStatus.getSlug());
        dto.setCreatedAt(taskStatus.getCreatedAt());
        return dto;
    }
}
