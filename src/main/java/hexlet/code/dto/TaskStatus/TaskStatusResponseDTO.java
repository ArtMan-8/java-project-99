package hexlet.code.dto.TaskStatus;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskStatusResponseDTO {
    private Long id;
    private String name;
    private String slug;
    private LocalDateTime createdAt;
}
