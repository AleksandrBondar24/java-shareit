package ru.practicum.shareit.user.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class UserDbRepository implements UserRepository {
    private final Map<Long, User> userRepository = new HashMap<>();
    private Long id = 1L;


    @Override
    public User create(User user) {
        user.setId(id);
        userRepository.put(user.getId(), user);
        id++;
        return user;
    }

    @Override
    public User findById(Long userId) {
        return userRepository.get(userId);
    }

    @Override
    public User update(User user) {
        return userRepository.put(user.getId(), user);
    }

    @Override
    public void delete(Long userId) {
        userRepository.remove(userId);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(userRepository.values());
    }
}
