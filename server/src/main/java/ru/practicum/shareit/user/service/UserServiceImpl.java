package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.exeption.NotFoundExceptionEntity;
import ru.practicum.shareit.user.UserMapper;


import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.user.UserMapper.toUser;
import static ru.practicum.shareit.user.UserMapper.toUserDto;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Transactional
    @Override
    public UserDto create(UserDto userDto) {
        return toUserDto(userRepository.save(toUser(userDto)));
    }

    @Override
    public UserDto findById(Long userId) {
        return toUserDto(userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundExceptionEntity("Пользователь с идентификатором : " + userId + " не найден.")));
    }

    @Transactional
    @Override
    public UserDto update(Long userId, UserDto userDto) {
        User userUpdate = toUser(userDto);
        userUpdate.setId(userId);
        User updatedUser = chekUser(userId);
        if (userUpdate.getName() == null)
            userUpdate.setName(updatedUser.getName());
        if (userUpdate.getEmail() == null)
            userUpdate.setEmail(updatedUser.getEmail());
        userRepository.save(userUpdate);
        return toUserDto(userUpdate);
    }

    @Transactional
    @Override
    public void delete(Long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    private User chekUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundExceptionEntity("Пользователь с идентификатором : " + userId + " не найден."));
    }
}
