package hexlet.code.service;

import hexlet.code.dto.TaskStatus.TaskStatusCreateDTO;
import hexlet.code.dto.TaskStatus.TaskStatusResponseDTO;
import hexlet.code.dto.TaskStatus.TaskStatusUpdateDTO;
import hexlet.code.mapper.TaskStatusMapper;
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
    private final TaskStatusMapper taskStatusMapper;

    public List<TaskStatusResponseDTO> getAllTaskStatuses() {
        return taskStatusMapper.toResponseDTOList(taskStatusRepository.findAll());
    }

    public Optional<TaskStatusResponseDTO> getTaskStatusById(Long id) {
        return taskStatusRepository.findById(id)
                .map(taskStatusMapper::toResponseDTO);
    }

    public TaskStatusResponseDTO createTaskStatus(TaskStatusCreateDTO taskStatusCreateDTO) {
        TaskStatus taskStatus = taskStatusMapper.toEntity(taskStatusCreateDTO);
        TaskStatus savedTaskStatus = taskStatusRepository.save(taskStatus);
        return taskStatusMapper.toResponseDTO(savedTaskStatus);
    }

    public Optional<TaskStatusResponseDTO> updateTaskStatus(Long id, TaskStatusUpdateDTO taskStatusUpdateDTO) {
        return taskStatusRepository.findById(id)
                .map(taskStatus -> {
                    taskStatusMapper.updateEntity(taskStatusUpdateDTO, taskStatus);
                    TaskStatus savedTaskStatus = taskStatusRepository.save(taskStatus);
                    return taskStatusMapper.toResponseDTO(savedTaskStatus);
                });
    }

    public boolean deleteTaskStatus(Long id) {
        if (taskStatusRepository.existsById(id)) {
            taskStatusRepository.deleteById(id);
            return true;
        }

        return false;
    }
}
