package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    User create(User user);

    User findById(Long userId);

    User update(Long userId, User user);

    void delete(Long userId);

    List<User> findAll();
}
