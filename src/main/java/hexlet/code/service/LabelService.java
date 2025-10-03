package hexlet.code.service;

import hexlet.code.dto.Label.LabelCreateDTO;
import hexlet.code.dto.Label.LabelResponseDTO;
import hexlet.code.dto.Label.LabelUpdateDTO;

import java.util.List;

public interface LabelService {
    List<LabelResponseDTO> getAllLabels();
    LabelResponseDTO getLabelById(Long id);
    LabelResponseDTO createLabel(LabelCreateDTO labelCreateDTO);
    LabelResponseDTO updateLabel(Long id, LabelUpdateDTO labelUpdateDTO);
    void deleteLabel(Long id);
}
