package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EmailConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserDto createUser(UserCreateDto userCreateDto) {
        if (userRepository.existsByEmail(userCreateDto.getEmail())) {
            throw new EmailConflictException("User with email " + userCreateDto.getEmail() + " already exists");
        }

        User user = userMapper.toUserFromCreateDto(userCreateDto);
        User savedUser = userRepository.save(user);
        return userMapper.toUserDto(savedUser);
    }

    @Override
    @Transactional
    public UserDto updateUser(Long userId, UserUpdateDto userUpdateDto) {
        User existingUser = findUserById(userId);

        // Проверка email только если он изменился и не пустой
        if (userUpdateDto.getEmail() != null &&
                !userUpdateDto.getEmail().isBlank() &&
                !userUpdateDto.getEmail().equalsIgnoreCase(existingUser.getEmail())) {

            if (userRepository.findByEmailAndIdNot(userUpdateDto.getEmail().toLowerCase(), userId).isPresent()) {
                throw new EmailConflictException("User with email " + userUpdateDto.getEmail() + " already exists");
            }
        }

        userMapper.updateUserFromUpdateDto(userUpdateDto, existingUser);
        User updatedUser = userRepository.save(existingUser);
        return userMapper.toUserDto(updatedUser);
    }

    @Override
    public UserDto getUserById(Long userId) {
        User user = findUserById(userId);
        return userMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toUserDto)
                .toList();
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found with id: " + userId);
        }
        userRepository.deleteById(userId);
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
    }
}