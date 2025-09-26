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

import java.util.List;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final LabelRepository labelRepository;
    private final TaskStatusRepository taskStatusRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;

    public List<TaskResponseDTO> getAllTasks(TaskFilterDTO filter) {
        Specification<Task> spec = TaskSpecification.buildSpecification(filter);
        return taskMapper.toResponseDTOList(taskRepository.findAll(spec));
    }

    public TaskResponseDTO getTaskById(Long id) {
        return taskRepository.findById(id)
                .map(taskMapper::toResponseDTO)
                .orElse(null);
    }

    public TaskResponseDTO createTask(TaskCreateDTO taskCreateDTO) {
        Task task = taskMapper.toEntity(taskCreateDTO);

        String statusSlug = taskCreateDTO.getStatus();
        TaskStatus taskStatus = taskStatusRepository.findBySlug(taskCreateDTO.getStatus())
                .orElseThrow(() -> new RuntimeException("TaskStatus not found with slug: " + statusSlug));
        task.setTaskStatus(taskStatus);

        var assigneeId = taskCreateDTO.getAssigneeId();
        if (assigneeId != null) {
            User assignee = userRepository.findById(assigneeId)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + assigneeId));
            task.setAssignee(assignee);
        }

        Set<Label> existingLabels = labelRepository.findByIdIn(taskCreateDTO.getTaskLabelIds());
        task.setLabels(existingLabels);

        taskRepository.save(task);
        return taskMapper.toResponseDTO(task);
    }

    public TaskResponseDTO updateTask(Long id, TaskUpdateDTO taskUpdateDTO) {
        Task task = taskRepository.findById(id).orElse(null);
        if (task == null) {
            return null;
        }

        taskMapper.updateEntity(taskUpdateDTO, task);

        String statusSlug = taskUpdateDTO.getStatus();
        TaskStatus taskStatus = taskStatusRepository.findBySlug(statusSlug)
                .orElseThrow(() -> new RuntimeException("TaskStatus not found with slug: " + statusSlug));
        task.setTaskStatus(taskStatus);

        var assigneeId = taskUpdateDTO.getAssigneeId();
        if (assigneeId != null) {
            User assignee = userRepository.findById(assigneeId)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + assigneeId));
            task.setAssignee(assignee);
        }

        var taskLabelIds = taskUpdateDTO.getTaskLabelIds();
        if (taskLabelIds != null) {
            Set<Label> existingLabels = labelRepository.findByIdIn(taskLabelIds);
            task.setLabels(existingLabels);
        }

        taskRepository.save(task);
        return taskMapper.toResponseDTO(task);
    }

    public boolean deleteTask(Long id) {
        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
            return true;
        }

        return false;
    }

}
