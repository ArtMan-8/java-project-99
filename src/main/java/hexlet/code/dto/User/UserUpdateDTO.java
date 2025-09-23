package hexlet.code.dto.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateDTO {
    @Email
    private String email;

    @Size(min = 1)
    private String firstName;

    @Size(min = 1)
    private String lastName;

    @Size(min = 3)
    private String password;
}
