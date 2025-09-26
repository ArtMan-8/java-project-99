package hexlet.code.service;

import hexlet.code.dto.Label.LabelCreateDTO;
import hexlet.code.dto.Label.LabelResponseDTO;
import hexlet.code.dto.Label.LabelUpdateDTO;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class LabelService {
    private final LabelRepository labelRepository;
    private final LabelMapper labelMapper;

    public List<LabelResponseDTO> getAllLabels() {
        return labelMapper.toResponseDTOList(labelRepository.findAll());
    }

    public LabelResponseDTO getLabelById(Long id) {
        return labelRepository.findById(id)
                .map(labelMapper::toResponseDTO)
                .orElse(null);
    }

    public LabelResponseDTO createLabel(LabelCreateDTO labelCreateDTO) {
        if (labelRepository.findByName(labelCreateDTO.getName()).isPresent()) {
            throw new RuntimeException("Label with this name already exists");
        }

        Label label = labelMapper.toEntity(labelCreateDTO);
        labelRepository.save(label);
        return labelMapper.toResponseDTO(label);
    }

    public LabelResponseDTO updateLabel(Long id, LabelUpdateDTO labelUpdateDTO) {
        Label label = labelRepository.findById(id).orElse(null);
        if (label == null) {
            return null;
        }

        labelMapper.updateEntity(labelUpdateDTO, label);
        labelRepository.save(label);
        return labelMapper.toResponseDTO(label);
    }

    public boolean deleteLabel(Long id) {
        if (labelRepository.existsById(id)) {
            labelRepository.deleteById(id);
            return true;
        }

        return false;
    }

}
