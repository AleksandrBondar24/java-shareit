package ru.practicum.shareit.userTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mvc;
    private final Long USERID = 1L;

    private final UserDto userDto = new UserDto(
            null,
            "Серж",
            "1234@mail.ru"
    );

    private final UserDto userDtoResponse = new UserDto(
            1L,
            "Серж",
            "1234@mail.ru"
    );

    @Test
    void createUser() throws Exception {
        when(userService.create(any()))
                .thenReturn(userDtoResponse);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDtoResponse.getId()))
                .andExpect(jsonPath("$.name").value(userDtoResponse.getName()))
                .andExpect(jsonPath("$.email").value(userDtoResponse.getEmail()));
        verify(userService, times(1)).create(userDto);
    }

    @Test
    void getUserById() throws Exception {
        when(userService.findById(USERID))
                .thenReturn(userDtoResponse);

        mvc.perform(get("/users/" + USERID)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .header("X-Sharer-User-Id", USERID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDtoResponse.getId()))
                .andExpect(jsonPath("$.name").value(userDtoResponse.getName()))
                .andExpect(jsonPath("$.email").value(userDtoResponse.getEmail()));
        verify(userService, times(1)).findById(USERID);
    }

    @Test
    void updateUser() throws Exception {
        when(userService.update(USERID, userDto))
                .thenReturn(userDtoResponse);

        mvc.perform(patch("/users/" + USERID)
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDtoResponse.getId()))
                .andExpect(jsonPath("$.name").value(userDtoResponse.getName()))
                .andExpect(jsonPath("$.email").value(userDtoResponse.getEmail()));
        verify(userService, times(1)).update(USERID, userDto);
    }

    @Test
    void deleteUser() throws Exception {
        mvc.perform(delete("/users/" + USERID)
                        .accept(MediaType.ALL))
                .andExpect(status().isOk());
        verify(userService, times(1)).delete(USERID);
    }

    @Test
    void getAllUser() throws Exception {
        when(userService.findAll())
                .thenReturn(Collections.emptyList());

        mvc.perform(get("/users")
                        .accept(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
        verify(userService, times(1)).findAll();
    }
}
