package hexlet.code.service;

import hexlet.code.dto.Task.TaskCreateDTO;
import hexlet.code.dto.Task.TaskResponseDTO;
import hexlet.code.dto.Task.TaskUpdateDTO;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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

    public List<TaskResponseDTO> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public Optional<TaskResponseDTO> getTaskById(Long id) {
        return taskRepository.findById(id)
                .map(this::toResponseDTO);
    }

    public TaskResponseDTO createTask(TaskCreateDTO taskCreateDTO) {
        Task task = new Task();
        task.setIndex(taskCreateDTO.getIndex());
        task.setTitle(taskCreateDTO.getTitle());
        task.setContent(taskCreateDTO.getContent());

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
        return toResponseDTO(savedTask);
    }

    public Optional<TaskResponseDTO> updateTask(Long id, TaskUpdateDTO taskUpdateDTO) {
        return taskRepository.findById(id)
                .map(task -> {
                    if (taskUpdateDTO.getIndex() != null) {
                        task.setIndex(taskUpdateDTO.getIndex());
                    }

                    if (taskUpdateDTO.getTitle() != null) {
                        task.setTitle(taskUpdateDTO.getTitle());
                    }

                    if (taskUpdateDTO.getContent() != null) {
                        task.setContent(taskUpdateDTO.getContent());
                    }

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
                    return toResponseDTO(savedTask);
                });
    }

    public boolean deleteTask(Long id) {
        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
            return true;
        }

        return false;
    }

    private TaskResponseDTO toResponseDTO(Task task) {
        TaskResponseDTO dto = new TaskResponseDTO();
        dto.setId(task.getId());
        dto.setIndex(task.getIndex());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setTitle(task.getTitle());
        dto.setContent(task.getContent());
        dto.setStatus(task.getTaskStatus().getSlug());

        if (task.getAssignee() != null) {
            dto.setAssigneeId(task.getAssignee().getId());
        }

        if (task.getLabels() != null) {
            Set<Long> labelIds = task.getLabels().stream()
                .map(Label::getId)
                .collect(java.util.stream.Collectors.toSet());
            dto.setLabelIds(labelIds);
        }

        return dto;
    }
}
