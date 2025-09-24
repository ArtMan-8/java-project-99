package hexlet.code.mapper;

import hexlet.code.dto.Label.LabelCreateDTO;
import hexlet.code.dto.Label.LabelResponseDTO;
import hexlet.code.dto.Label.LabelUpdateDTO;
import hexlet.code.model.Label;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface LabelMapper {

    LabelResponseDTO toResponseDTO(Label label);

    List<LabelResponseDTO> toResponseDTOList(List<Label> labels);

    Label toEntity(LabelCreateDTO labelCreateDTO);

    void updateEntity(LabelUpdateDTO labelUpdateDTO, @MappingTarget Label label);
}
