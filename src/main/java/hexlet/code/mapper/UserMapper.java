package hexlet.code.mapper;

import hexlet.code.dto.User.UserCreateDTO;
import hexlet.code.dto.User.UserResponseDTO;
import hexlet.code.dto.User.UserUpdateDTO;
import hexlet.code.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    UserResponseDTO toResponseDTO(User user);

    List<UserResponseDTO> toResponseDTOList(List<User> users);

    User toEntity(UserCreateDTO userCreateDTO);

    void updateEntity(UserUpdateDTO userUpdateDTO, @MappingTarget User user);
}
