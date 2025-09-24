package hexlet.code.dto.Task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class TaskCreateDTO {
    @NotNull
    private Integer index;

    private Long assigneeId;

    @NotBlank
    @Size(min = 1)
    private String title;

    private String content;

    @NotBlank
    @Size(min = 1)
    private String status;

    private Set<Long> labelIds;
}
