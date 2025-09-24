package hexlet.code.dto.Label;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LabelResponseDTO {
    private Long id;
    private String name;
    private LocalDateTime createdAt;
}
