package ru.practicum.shareit.user.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toUserDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "userCreateDto.name")
    @Mapping(target = "email", source = "userCreateDto.email")
    User toUserFromCreateDto(UserCreateDto userCreateDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "userUpdateDto.name")
    @Mapping(target = "email", source = "userUpdateDto.email")
    User toUserFromUpdateDto(UserUpdateDto userUpdateDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "userUpdateDto.name")
    @Mapping(target = "email", source = "userUpdateDto.email")
    void updateUserFromUpdateDto(UserUpdateDto userUpdateDto, @MappingTarget User user);
}