package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.mapper.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.util.mapper.UserMapper.toUser;
import static ru.practicum.shareit.util.mapper.UserMapper.toUserDto;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto create(UserDto userDto) {
        User user = userRepository.create(toUser(userDto));
        return toUserDto(user);
    }

    @Override
    public UserDto findById(Long userId) {
        User user = userRepository.findById(userId);
        return toUserDto(user);
    }

    @Override
    public UserDto update(Long userId, UserDto userDto) {
        User user = userRepository.update(userId, toUser(userDto));
        return toUserDto(user);
    }

    @Override
    public void delete(Long userId) {
        userRepository.delete(userId);
    }

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }
}
