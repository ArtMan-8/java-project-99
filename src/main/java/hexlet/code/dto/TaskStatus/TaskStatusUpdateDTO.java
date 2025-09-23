package hexlet.code.dto.TaskStatus;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TaskStatusUpdateDTO {
    @Size(min = 1)
    private String name;

    @Size(min = 1)
    private String slug;
}
