package hexlet.code.dto.Label;

import lombok.Data;

import java.time.LocalDate;

@Data
public class LabelResponseDTO {
    private Long id;
    private String name;
    private LocalDate createdAt;
}
