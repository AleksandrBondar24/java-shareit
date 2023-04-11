package ru.practicum.shareit.user.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.exeption.NotFoundExceptionEntity;
import ru.practicum.shareit.util.exeption.ValidationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class UserDbRepository implements UserRepository {
    private final Map<Long, User> userRepository = new HashMap<>();
    private Long id = 1L;


    @Override
    public User create(User user) {
        user.setId(id);
        validateEmail(user);
        userRepository.put(user.getId(), user);
        id++;
        return user;
    }

    @Override
    public User findById(Long userId) {
        User user = userRepository.get(userId);
        if (user == null) {
            throw new NotFoundExceptionEntity("Пользователь с идентификатором : " + userId + " не найден.");
        }
        return user;
    }

    @Override
    public User update(Long userId, User user) {
        User updatedUser = userRepository.get(userId);
        if (updatedUser == null) {
            throw new NotFoundExceptionEntity("Пользователь с идентификатором : " + userId + " не найден.");
        }
        if (!updatedUser.getEmail().equals(user.getEmail())) {
            validateEmail(user);
        }
        updatedUser.setId(userId);
        if (user.getName() != null) {
            updatedUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            updatedUser.setEmail(user.getEmail());
        }
        return updatedUser;
    }

    @Override
    public void delete(Long userId) {
        userRepository.remove(userId);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(userRepository.values());
    }

    private void validateEmail(User user) {
        userRepository.values().stream().map(User::getEmail).forEach(u -> {
            if (u.equals(user.getEmail())) throw new ValidationException("Такой email уже сущетсвует");
        });
    }
}
