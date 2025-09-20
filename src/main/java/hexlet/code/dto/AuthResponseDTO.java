package hexlet.code.dto;

import lombok.Data;

@Data
public class AuthResponseDTO {
    private String token;
    private String email;
    private String firstName;
    private String lastName;
}
