package hexlet.code.dto.Auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthRequestDTO {
    @Email
    @NotBlank
    private String username;

    @NotBlank
    private String password;
}
