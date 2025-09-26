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

@Service
@Transactional
@RequiredArgsConstructor
public class TaskStatusService {
    private final TaskStatusRepository taskStatusRepository;
    private final TaskStatusMapper taskStatusMapper;

    public List<TaskStatusResponseDTO> getAllTaskStatuses() {
        return taskStatusMapper.toResponseDTOList(taskStatusRepository.findAll());
    }

    public TaskStatusResponseDTO getTaskStatusById(Long id) {
        return taskStatusRepository.findById(id)
                .map(taskStatusMapper::toResponseDTO)
                .orElse(null);
    }

    public TaskStatusResponseDTO createTaskStatus(TaskStatusCreateDTO taskStatusCreateDTO) {
        TaskStatus taskStatus = taskStatusMapper.toEntity(taskStatusCreateDTO);
        taskStatusRepository.save(taskStatus);
        return taskStatusMapper.toResponseDTO(taskStatus);
    }

    public TaskStatusResponseDTO updateTaskStatus(Long id, TaskStatusUpdateDTO taskStatusUpdateDTO) {
        TaskStatus taskStatus = taskStatusRepository.findById(id).orElse(null);
        if (taskStatus == null) {
            return null;
        }

        taskStatusMapper.updateEntity(taskStatusUpdateDTO, taskStatus);
        taskStatusRepository.save(taskStatus);
        return taskStatusMapper.toResponseDTO(taskStatus);
    }

    public boolean deleteTaskStatus(Long id) {
        if (taskStatusRepository.existsById(id)) {
            taskStatusRepository.deleteById(id);
            return true;
        }

        return false;
    }
}
