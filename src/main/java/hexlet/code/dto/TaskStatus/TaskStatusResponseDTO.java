package hexlet.code.dto.TaskStatus;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TaskStatusResponseDTO {
    private Long id;
    private String name;
    private String slug;
    private LocalDate createdAt;
}
