package hexlet.code.service;

import hexlet.code.dto.Label.LabelCreateDTO;
import hexlet.code.dto.Label.LabelResponseDTO;
import hexlet.code.dto.Label.LabelUpdateDTO;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class LabelService {
    private final LabelRepository labelRepository;

    public List<LabelResponseDTO> getAllLabels() {
        return labelRepository.findAll().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public Optional<LabelResponseDTO> getLabelById(Long id) {
        return labelRepository.findById(id)
                .map(this::toResponseDTO);
    }

    public LabelResponseDTO createLabel(LabelCreateDTO labelCreateDTO) {
        String labelName = labelCreateDTO.getName();
        if (labelRepository.findByName(labelName).isPresent()) {
            throw new DataIntegrityViolationException("Label with name '" + labelName + "' already exists");
        }

        Label label = new Label();
        label.setName(labelName);

        Label savedLabel = labelRepository.save(label);
        return toResponseDTO(savedLabel);
    }

    public Optional<LabelResponseDTO> updateLabel(Long id, LabelUpdateDTO labelUpdateDTO) {
        return labelRepository.findById(id)
                .map(label -> {
                    if (labelUpdateDTO.getName() != null) {
                        label.setName(labelUpdateDTO.getName());
                    }

                    Label savedLabel = labelRepository.save(label);
                    return toResponseDTO(savedLabel);
                });
    }

    public boolean deleteLabel(Long id) {
        if (labelRepository.existsById(id)) {
            labelRepository.deleteById(id);
            return true;
        }

        return false;
    }

    private LabelResponseDTO toResponseDTO(Label label) {
        LabelResponseDTO dto = new LabelResponseDTO();
        dto.setId(label.getId());
        dto.setName(label.getName());
        dto.setCreatedAt(label.getCreatedAt());
        return dto;
    }
}
