package hexlet.code.service;

import hexlet.code.dto.Task.TaskCreateDTO;
import hexlet.code.dto.Task.TaskResponseDTO;
import hexlet.code.dto.Task.TaskUpdateDTO;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.specification.TaskSpecification;
import hexlet.code.dto.Task.TaskFilterDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskStatusRepository taskStatusRepository;
    private final LabelRepository labelRepository;
    private final TaskMapper taskMapper;

    public List<TaskResponseDTO> getAllTasks(TaskFilterDTO filter) {
        Specification<Task> spec = TaskSpecification.buildSpecification(filter);
        return taskMapper.toResponseDTOList(taskRepository.findAll(spec));
    }

    public Optional<TaskResponseDTO> getTaskById(Long id) {
        return taskRepository.findById(id)
                .map(taskMapper::toResponseDTO);
    }

    public TaskResponseDTO createTask(TaskCreateDTO taskCreateDTO) {
        Task task = taskMapper.toEntity(taskCreateDTO);

        if (taskCreateDTO.getAssigneeId() != null) {
            User assignee = userRepository.findById(taskCreateDTO.getAssigneeId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + taskCreateDTO.getAssigneeId()));
            task.setAssignee(assignee);
        }

        TaskStatus taskStatus = taskStatusRepository.findBySlug(taskCreateDTO.getStatus())
            .orElseThrow(() -> new RuntimeException("TaskStatus not found with slug: " + taskCreateDTO.getStatus()));
        task.setTaskStatus(taskStatus);

        if (taskCreateDTO.getLabelIds() != null && !taskCreateDTO.getLabelIds().isEmpty()) {
            Set<Label> labels = new HashSet<>();
            for (Long labelId : taskCreateDTO.getLabelIds()) {
                Label label = labelRepository.findById(labelId)
                    .orElseThrow(() -> new RuntimeException("Label not found with id: " + labelId));
                labels.add(label);
            }
            task.setLabels(labels);
        }

        Task savedTask = taskRepository.save(task);
        return taskMapper.toResponseDTO(savedTask);
    }

    public Optional<TaskResponseDTO> updateTask(Long id, TaskUpdateDTO taskUpdateDTO) {
        return taskRepository.findById(id)
                .map(task -> {
                    taskMapper.updateEntity(taskUpdateDTO, task);

                    var assigneeId = taskUpdateDTO.getAssigneeId();
                    if (assigneeId != null) {
                        User assignee = userRepository.findById(assigneeId)
                            .orElseThrow(() -> new RuntimeException("User not found with id: " + assigneeId));
                        task.setAssignee(assignee);
                    }

                    var statusName = taskUpdateDTO.getStatus();
                    if (statusName != null) {
                        TaskStatus taskStatus = taskStatusRepository.findBySlug(statusName)
                            .orElseThrow(() -> new RuntimeException("TaskStatus not found with slug: " + statusName));
                        task.setTaskStatus(taskStatus);
                    }

                    if (taskUpdateDTO.getLabelIds() != null) {
                        Set<Label> labels = new HashSet<>();
                        for (Long labelId : taskUpdateDTO.getLabelIds()) {
                            Label label = labelRepository.findById(labelId)
                                .orElseThrow(() -> new RuntimeException("Label not found with id: " + labelId));
                            labels.add(label);
                        }
                        task.setLabels(labels);
                    }

                    Task savedTask = taskRepository.save(task);
                    return taskMapper.toResponseDTO(savedTask);
                });
    }

    public boolean deleteTask(Long id) {
        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
            return true;
        }

        return false;
    }

}
