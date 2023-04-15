package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.util.exeption.NotFoundException;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final UserServiceImpl userService;

    @PostMapping
    public UserDto create(@Valid @RequestBody UserDto userDto) {
        if (userDto.getEmail() == null) {
            throw new NotFoundException("Отсутствует email");
        }
        UserDto createdUser = userService.create(userDto);
        log.debug("Создан пользователь с идентификатором : {} ", createdUser.getId());
        return createdUser;
    }

    @GetMapping("/{userId}")
    public UserDto findById(@PathVariable Long userId) {
        UserDto receivedUser = userService.findById(userId);
        log.debug("Получен пользователь с идентификатором : {}", userId);
        return receivedUser;
    }

    @PatchMapping("/{userId}")
    public UserDto update(@Valid @RequestBody UserDto userDto,
                          @PathVariable Long userId) {
        UserDto updatedUser = userService.update(userId, userDto);
        log.debug("Обновлен пользователь с идентификатором : {}", userId);
        return updatedUser;
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {
        userService.delete(userId);
        log.debug("Удален пользователь с идентификатором : {}", userId);
    }

    @GetMapping
    public List<UserDto> findAll() {
        List<UserDto> listAllUser = userService.findAll();
        log.debug("Получен список всех пользоателей");
        return listAllUser;
    }
}
