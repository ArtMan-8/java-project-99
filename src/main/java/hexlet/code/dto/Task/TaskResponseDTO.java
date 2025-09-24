package hexlet.code.dto.Task;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class TaskResponseDTO {
    private Long id;
    private Integer index;
    private LocalDateTime createdAt;
    private Long assigneeId;
    private String title;
    private String content;
    private String status;
    private Set<Long> labelIds;
}
