package hexlet.code.service.impl;

import hexlet.code.dto.Task.TaskCreateDTO;
import hexlet.code.dto.Task.TaskFilterDTO;
import hexlet.code.dto.Task.TaskResponseDTO;
import hexlet.code.dto.Task.TaskUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.TaskService;
import hexlet.code.specification.TaskSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskStatusRepository taskStatusRepository;
    private final LabelRepository labelRepository;
    private final TaskMapper taskMapper;

    @Override
    public List<TaskResponseDTO> getAllTasks(TaskFilterDTO filter) {
        Specification<Task> spec = TaskSpecification.buildSpecification(filter);
        return taskMapper.toResponseDTOList(taskRepository.findAll(spec));
    }

    @Override
    public TaskResponseDTO getTaskById(Long id) {
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        return taskMapper.toResponseDTO(task);
    }

    @Override
    public TaskResponseDTO createTask(TaskCreateDTO taskCreateDTO) {
        Task task = taskMapper.toEntity(taskCreateDTO);

        var assigneeId = taskCreateDTO.getAssigneeId();
        if (assigneeId != null) {
            User assignee = userRepository.findById(assigneeId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + assigneeId));
            task.setAssignee(assignee);
        }

        var statusName = taskCreateDTO.getStatus();
        TaskStatus taskStatus = taskStatusRepository.findBySlug(statusName)
            .orElseThrow(() -> new ResourceNotFoundException("TaskStatus not found with slug: " + statusName));
        task.setTaskStatus(taskStatus);

        var taskLabelIds = taskCreateDTO.getTaskLabelIds();
        if (taskLabelIds != null && !taskLabelIds.isEmpty()) {
            Set<Label> labels = new HashSet<>();
            for (Long labelId : taskLabelIds) {
                Label label = labelRepository.findById(labelId)
                    .orElseThrow(() -> new ResourceNotFoundException("Label not found with id: " + labelId));
                labels.add(label);
            }
            task.setLabels(labels);
        }

        Task savedTask = taskRepository.save(task);
        return taskMapper.toResponseDTO(savedTask);
    }

    @Override
    public TaskResponseDTO updateTask(Long id, TaskUpdateDTO taskUpdateDTO) {
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        taskMapper.updateEntity(taskUpdateDTO, task);

        var assigneeId = taskUpdateDTO.getAssigneeId();
        if (assigneeId != null) {
            User assignee = userRepository.findById(assigneeId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + assigneeId));
            task.setAssignee(assignee);
        }

        var statusName = taskUpdateDTO.getStatus();
        if (statusName != null) {
            TaskStatus taskStatus = taskStatusRepository.findBySlug(statusName)
                .orElseThrow(() -> new ResourceNotFoundException("TaskStatus not found with slug: " + statusName));
            task.setTaskStatus(taskStatus);
        }

        if (taskUpdateDTO.getTaskLabelIds() != null) {
            Set<Label> labels = new HashSet<>();
            for (Long labelId : taskUpdateDTO.getTaskLabelIds()) {
                Label label = labelRepository.findById(labelId)
                    .orElseThrow(() -> new ResourceNotFoundException("Label not found with id: " + labelId));
                labels.add(label);
            }
            task.setLabels(labels);
        }

        Task savedTask = taskRepository.save(task);
        return taskMapper.toResponseDTO(savedTask);
    }

    @Override
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }
}


