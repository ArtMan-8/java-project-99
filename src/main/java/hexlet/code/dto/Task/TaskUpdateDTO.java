package hexlet.code.dto.Task;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TaskUpdateDTO {
    private Integer index;

    private Long assigneeId;

    @Size(min = 1)
    private String title;

    private String content;

    @Size(min = 1)
    private String status;
}
