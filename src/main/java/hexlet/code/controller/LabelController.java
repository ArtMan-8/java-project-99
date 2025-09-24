package hexlet.code.controller;

import hexlet.code.dto.Label.LabelCreateDTO;
import hexlet.code.dto.Label.LabelResponseDTO;
import hexlet.code.dto.Label.LabelUpdateDTO;
import hexlet.code.service.LabelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/labels")
@RequiredArgsConstructor
@Validated
public class LabelController {
    private final LabelService labelService;

    @GetMapping
    public ResponseEntity<List<LabelResponseDTO>> getAllLabels() {
        List<LabelResponseDTO> labels = labelService.getAllLabels();
        long totalCount = labelService.getTotalLabelsCount();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(totalCount))
                .body(labels);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LabelResponseDTO> getLabelById(@PathVariable Long id) {
        Optional<LabelResponseDTO> label = labelService.getLabelById(id);
        return label.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LabelResponseDTO createLabel(@Valid @RequestBody LabelCreateDTO labelCreateDTO) {
        return labelService.createLabel(labelCreateDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LabelResponseDTO> updateLabel(
            @PathVariable Long id,
            @Valid @RequestBody LabelUpdateDTO labelUpdateDTO) {
        Optional<LabelResponseDTO> label = labelService.updateLabel(id, labelUpdateDTO);
        return label.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLabel(@PathVariable Long id) {
        boolean deleted = labelService.deleteLabel(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
