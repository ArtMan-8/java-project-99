package hexlet.code.service;

import hexlet.code.dto.Label.LabelCreateDTO;
import hexlet.code.dto.Label.LabelResponseDTO;
import hexlet.code.dto.Label.LabelUpdateDTO;
import hexlet.code.exception.LabelHasTasksException;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
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
    private final TaskRepository taskRepository;
    private final LabelMapper labelMapper;

    public List<LabelResponseDTO> getAllLabels() {
        return labelMapper.toResponseDTOList(labelRepository.findAll());
    }

    public Optional<LabelResponseDTO> getLabelById(Long id) {
        return labelRepository.findById(id)
                .map(labelMapper::toResponseDTO);
    }

    public LabelResponseDTO createLabel(LabelCreateDTO labelCreateDTO) {
        String labelName = labelCreateDTO.getName();
        if (labelRepository.findByName(labelName).isPresent()) {
            throw new DataIntegrityViolationException("Label with name '" + labelName + "' already exists");
        }

        Label label = labelMapper.toEntity(labelCreateDTO);
        Label savedLabel = labelRepository.save(label);
        return labelMapper.toResponseDTO(savedLabel);
    }

    public Optional<LabelResponseDTO> updateLabel(Long id, LabelUpdateDTO labelUpdateDTO) {
        return labelRepository.findById(id)
                .map(label -> {
                    labelMapper.updateEntity(labelUpdateDTO, label);
                    Label savedLabel = labelRepository.save(label);
                    return labelMapper.toResponseDTO(savedLabel);
                });
    }

    public boolean deleteLabel(Long id) {
        if (labelRepository.existsById(id)) {
            boolean hasTasks = taskRepository.existsByLabelsId(id);
            if (hasTasks) {
                throw new LabelHasTasksException(
                    "Cannot delete label with id " + id + " because there are tasks with this label");
            }

            labelRepository.deleteById(id);
            return true;
        }

        return false;
    }

}
