package hexlet.code.dto.Label;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LabelUpdateDTO {
    @Size(min = 3, max = 1000)
    private String name;
}
