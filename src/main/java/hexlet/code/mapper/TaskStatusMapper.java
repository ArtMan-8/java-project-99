package hexlet.code.mapper;

import hexlet.code.dto.TaskStatus.TaskStatusCreateDTO;
import hexlet.code.dto.TaskStatus.TaskStatusResponseDTO;
import hexlet.code.dto.TaskStatus.TaskStatusUpdateDTO;
import hexlet.code.model.TaskStatus;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TaskStatusMapper {

    TaskStatusResponseDTO toResponseDTO(TaskStatus taskStatus);

    List<TaskStatusResponseDTO> toResponseDTOList(List<TaskStatus> taskStatuses);

    TaskStatus toEntity(TaskStatusCreateDTO taskStatusCreateDTO);

    void updateEntity(TaskStatusUpdateDTO taskStatusUpdateDTO, @MappingTarget TaskStatus taskStatus);
}
