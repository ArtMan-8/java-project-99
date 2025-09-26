package hexlet.code.mapper;

import hexlet.code.dto.Label.LabelCreateDTO;
import hexlet.code.dto.Label.LabelResponseDTO;
import hexlet.code.dto.Label.LabelUpdateDTO;
import hexlet.code.model.Label;
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
public interface LabelMapper {
    LabelResponseDTO toResponseDTO(Label label);

    List<LabelResponseDTO> toResponseDTOList(List<Label> labels);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Label toEntity(LabelCreateDTO labelCreateDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntity(LabelUpdateDTO labelUpdateDTO, @MappingTarget Label label);
}
