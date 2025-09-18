package hexlet.code.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserCreateDTO {
    @NotBlank
    @Email
    private String email;

    @Size(min = 1)
    private String firstName;

    @Size(min = 1)
    private String lastName;

    @NotBlank
    @Size(min = 3)
    private String password;
}
