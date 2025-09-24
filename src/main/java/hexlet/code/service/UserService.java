package hexlet.code.service;

import hexlet.code.dto.User.UserCreateDTO;
import hexlet.code.dto.User.UserResponseDTO;
import hexlet.code.dto.User.UserUpdateDTO;
import hexlet.code.mapper.UserMapper;
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
    private final UserMapper userMapper;

    public List<UserResponseDTO> getAllUsers() {
        return userMapper.toResponseDTOList(userRepository.findAll());
    }

    public long getTotalUsersCount() {
        return userRepository.count();
    }

    public Optional<UserResponseDTO> getUserById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toResponseDTO);
    }

    public UserResponseDTO createUser(UserCreateDTO userCreateDTO) {
        User user = userMapper.toEntity(userCreateDTO);
        user.setPassword(passwordEncoder.encode(userCreateDTO.getPassword()));
        User savedUser = userRepository.save(user);
        return userMapper.toResponseDTO(savedUser);
    }

    public Optional<UserResponseDTO> updateUser(Long id, UserUpdateDTO userUpdateDTO) {
        return userRepository.findById(id)
                .map(user -> {
                    userMapper.updateEntity(userUpdateDTO, user);
                    if (userUpdateDTO.getPassword() != null) {
                        user.setPassword(passwordEncoder.encode(userUpdateDTO.getPassword()));
                    }
                    User savedUser = userRepository.save(user);
                    return userMapper.toResponseDTO(savedUser);
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
}
