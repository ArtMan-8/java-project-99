package hexlet.code.dto.Task;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskResponseDTO {
    private Long id;
    private Integer index;
    private LocalDateTime createdAt;
    private Long assigneeId;
    private String title;
    private String content;
    private String status;
}
