package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.exeption.NotFoundExceptionEntity;
import ru.practicum.shareit.util.exeption.ValidationException;
import ru.practicum.shareit.util.mapper.UserMapper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.practicum.shareit.util.mapper.UserMapper.toUser;
import static ru.practicum.shareit.util.mapper.UserMapper.toUserDto;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final Set<String> emails = new HashSet<>();

    @Override
    public UserDto create(UserDto userDto) {
        validateEmail(userDto);
        emails.add(userDto.getEmail());
        User user = userRepository.create(toUser(userDto));
        return toUserDto(user);
    }

    @Override
    public UserDto findById(Long userId) {
        User user = userRepository.findById(userId);
        if (user == null) {
            throw new NotFoundExceptionEntity("Пользователь с идентификатором : " + userId + " не найден.");
        }
        return toUserDto(user);
    }

    @Override
    public UserDto update(Long userId, UserDto userDto) {
        User updatedUser = userRepository.findById(userId);
        updatedUser.setId(userId);
        if (userDto.getName() != null)
            updatedUser.setName(userDto.getName());
        if (userDto.getEmail() != null) {
            emails.remove(updatedUser.getEmail());
            validateEmail(userDto);
            updatedUser.setEmail(userDto.getEmail());
            emails.add(userDto.getEmail());
        }
        User user = userRepository.update(updatedUser);
        return toUserDto(user);
    }

    @Override
    public void delete(Long userId) {
        emails.remove(findById(userId).getEmail());
        userRepository.delete(userId);
    }

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    private void validateEmail(UserDto userDto) {
        if (emails.contains(userDto.getEmail()))
            throw new ValidationException("Такой email уже сущетсвует");
    }
}
