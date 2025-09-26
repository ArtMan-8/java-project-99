package hexlet.code.dto.Task;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class TaskUpdateDTO {
    private Integer index;

    private Long assigneeId;

    @Size(min = 1)
    private String title;

    private String content;

    @Size(min = 1)
    private String status;

    private Set<Long> taskLabelIds;
}
