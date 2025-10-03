package hexlet.code.service.impl;

import hexlet.code.dto.Label.LabelCreateDTO;
import hexlet.code.dto.Label.LabelResponseDTO;
import hexlet.code.dto.Label.LabelUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import hexlet.code.service.LabelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class LabelServiceImpl implements LabelService {
    private final LabelRepository labelRepository;
    private final LabelMapper labelMapper;

    @Override
    public List<LabelResponseDTO> getAllLabels() {
        return labelMapper.toResponseDTOList(labelRepository.findAll());
    }

    @Override
    public LabelResponseDTO getLabelById(Long id) {
        Label label = labelRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Label not found with id: " + id));

        return labelMapper.toResponseDTO(label);
    }

    @Override
    public LabelResponseDTO createLabel(LabelCreateDTO labelCreateDTO) {
        Label label = labelMapper.toEntity(labelCreateDTO);
        Label savedLabel = labelRepository.save(label);
        return labelMapper.toResponseDTO(savedLabel);
    }

    @Override
    public LabelResponseDTO updateLabel(Long id, LabelUpdateDTO labelUpdateDTO) {
        Label label = labelRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Label not found with id: " + id));

        labelMapper.updateEntity(labelUpdateDTO, label);
        Label savedLabel = labelRepository.save(label);
        return labelMapper.toResponseDTO(savedLabel);
    }

    @Override
    public void deleteLabel(Long id) {
        labelRepository.deleteById(id);
    }
}


