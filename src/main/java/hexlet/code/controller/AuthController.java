package hexlet.code.controller;

import hexlet.code.dto.Auth.AuthRequestDTO;
import hexlet.code.util.JwtUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/login")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @PostMapping
    public String login(@Valid @RequestBody AuthRequestDTO authRequest) {
        Authentication authToken = new UsernamePasswordAuthenticationToken(
            authRequest.getUsername(),
            authRequest.getPassword()
        );

        authenticationManager.authenticate(authToken);

        return jwtUtils.generateToken(authRequest.getUsername());
    }
}
