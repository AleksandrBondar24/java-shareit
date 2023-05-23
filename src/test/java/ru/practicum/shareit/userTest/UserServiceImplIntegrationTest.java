package ru.practicum.shareit.userTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exeption.NotFoundExceptionEntity;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImplIntegrationTest {

    private UserService userService;

    private final UserRepository userRepository;
    private UserDto userDto;

    @BeforeEach
    public void createEnvironment() {
        userService = new UserServiceImpl(userRepository);
        userDto = new UserDto();
        userDto.setName("Серж");
        userDto.setEmail("12345@mail.ru");
    }

    @Test
    void saveUser() {
        var result = userService.create(userDto);

        assertThat(result, notNullValue());
        assertThat(result.getName(), equalTo(userDto.getName()));
        assertThat(result.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void getUser() {
        var userId = userService.create(userDto).getId();
        var result = userService.findById(userId);

        assertThat(result, notNullValue());
        assertThat(result.getId(), equalTo(userId));
    }

    @Test
    void updateUser() {
        var userId = userService.create(userDto).getId();
        userDto.setName("Alex");
        var result = userService.update(userId, userDto);

        assertThat(result, notNullValue());
        assertThat(result.getName(), equalTo("Alex"));
    }

    @Test
    void deleteUser() {
        var userId = userService.create(userDto).getId();
        userService.delete(userId);

        Assertions.assertThrows(NotFoundExceptionEntity.class, () -> userService.findById(userId));
    }

    @Test
    void getAllUser() {
        var result = userService.findAll();

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(0));
    }
}
