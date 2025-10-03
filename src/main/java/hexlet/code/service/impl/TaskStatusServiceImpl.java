package hexlet.code.service.impl;

import hexlet.code.dto.TaskStatus.TaskStatusCreateDTO;
import hexlet.code.dto.TaskStatus.TaskStatusResponseDTO;
import hexlet.code.dto.TaskStatus.TaskStatusUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.service.TaskStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TaskStatusServiceImpl implements TaskStatusService {
    private final TaskStatusRepository taskStatusRepository;
    private final TaskStatusMapper taskStatusMapper;

    @Override
    public List<TaskStatusResponseDTO> getAllTaskStatuses() {
        return taskStatusMapper.toResponseDTOList(taskStatusRepository.findAll());
    }

    @Override
    public TaskStatusResponseDTO getTaskStatusById(Long id) {
        TaskStatus taskStatus = taskStatusRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("TaskStatus not found with id: " + id));

        return taskStatusMapper.toResponseDTO(taskStatus);
    }

    @Override
    public TaskStatusResponseDTO createTaskStatus(TaskStatusCreateDTO taskStatusCreateDTO) {
        TaskStatus taskStatus = taskStatusMapper.toEntity(taskStatusCreateDTO);
        TaskStatus savedTaskStatus = taskStatusRepository.save(taskStatus);
        return taskStatusMapper.toResponseDTO(savedTaskStatus);
    }

    @Override
    public TaskStatusResponseDTO updateTaskStatus(Long id, TaskStatusUpdateDTO taskStatusUpdateDTO) {
        TaskStatus taskStatus = taskStatusRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("TaskStatus not found with id: " + id));

        taskStatusMapper.updateEntity(taskStatusUpdateDTO, taskStatus);
        TaskStatus savedTaskStatus = taskStatusRepository.save(taskStatus);
        return taskStatusMapper.toResponseDTO(savedTaskStatus);
    }

    @Override
    public void deleteTaskStatus(Long id) {
        taskStatusRepository.deleteById(id);
    }
}


