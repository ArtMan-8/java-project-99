package hexlet.code.mapper;

import hexlet.code.dto.TaskStatus.TaskStatusCreateDTO;
import hexlet.code.dto.TaskStatus.TaskStatusResponseDTO;
import hexlet.code.dto.TaskStatus.TaskStatusUpdateDTO;
import hexlet.code.model.TaskStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface TaskStatusMapper {
    TaskStatusResponseDTO toResponseDTO(TaskStatus taskStatus);

    List<TaskStatusResponseDTO> toResponseDTOList(List<TaskStatus> taskStatuses);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    TaskStatus toEntity(TaskStatusCreateDTO taskStatusCreateDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntity(TaskStatusUpdateDTO taskStatusUpdateDTO, @MappingTarget TaskStatus taskStatus);
}
