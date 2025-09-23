package hexlet.code.service;

import hexlet.code.dto.User.UserCreateDTO;
import hexlet.code.dto.User.UserResponseDTO;
import hexlet.code.dto.User.UserUpdateDTO;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public long getTotalUsersCount() {
        return userRepository.count();
    }

    public Optional<UserResponseDTO> getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::toResponseDTO);
    }

    public UserResponseDTO createUser(UserCreateDTO userCreateDTO) {
        User user = new User();
        user.setEmail(userCreateDTO.getEmail());
        user.setFirstName(userCreateDTO.getFirstName());
        user.setLastName(userCreateDTO.getLastName());
        user.setPassword(passwordEncoder.encode(userCreateDTO.getPassword()));

        User savedUser = userRepository.save(user);
        return toResponseDTO(savedUser);
    }

    public Optional<UserResponseDTO> updateUser(Long id, UserUpdateDTO userUpdateDTO) {
        return userRepository.findById(id)
                .map(user -> {
                    if (userUpdateDTO.getEmail() != null) {
                        user.setEmail(userUpdateDTO.getEmail());
                    }

                    if (userUpdateDTO.getFirstName() != null) {
                        user.setFirstName(userUpdateDTO.getFirstName());
                    }

                    if (userUpdateDTO.getLastName() != null) {
                        user.setLastName(userUpdateDTO.getLastName());
                    }

                    if (userUpdateDTO.getPassword() != null) {
                        user.setPassword(passwordEncoder.encode(userUpdateDTO.getPassword()));
                    }

                    User savedUser = userRepository.save(user);
                    return toResponseDTO(savedUser);
                });
    }

    public boolean deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }

        return false;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(new SimpleGrantedAuthority("ROLE_USER"))
                .build();
    }

    private UserResponseDTO toResponseDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }
}
