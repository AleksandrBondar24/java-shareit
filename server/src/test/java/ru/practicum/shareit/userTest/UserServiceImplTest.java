package ru.practicum.shareit.userTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exeption.NotFoundExceptionEntity;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.user.UserMapper.toUser;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImplTest {
    @Mock
    private final UserRepository userRepository;
    private UserService userService;
    private UserDto userDto;

    @BeforeEach
    public void createEnvironment() {
        userService = new UserServiceImpl(userRepository);
        userDto = new UserDto();
        userDto.setName("Серж");
        userDto.setEmail("12345@mail.ru");

        when(userRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void saveUser() {
        var result = userService.create(userDto);

        assertThat(result, notNullValue());
        assertThat(result.getName(), equalTo(userDto.getName()));
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void getUser() {
        var userId = 99L;

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        verify(userRepository, times(0)).findById(userId);
        Assertions.assertThrows(NotFoundExceptionEntity.class, () -> userService.findById(userId));
    }

    @Test
    void shouldNotUpdateNameOrEmailIfTheyAreNull() {
        var result = userService.create(userDto);
        userDto.setEmail(null);
        userDto.setName(null);
        var userId = 1L;

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(toUser(result)));
        var resultUpdate = userService.update(userId, userDto);

        assertThat(resultUpdate, notNullValue());
        assertThat(resultUpdate.getName(), equalTo(result.getName()));
        assertThat(resultUpdate.getEmail(), equalTo(result.getEmail()));
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getAllUser() {
        when(userRepository.findAll())
                .thenReturn(Collections.emptyList());

        var result = userService.findAll();

        assertThat(result, notNullValue());
        assertThat("isEmpty", result.isEmpty());
    }
}
