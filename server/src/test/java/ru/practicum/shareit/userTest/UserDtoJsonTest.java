package ru.practicum.shareit.userTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.user.dto.UserDto;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@JsonTest
public class UserDtoJsonTest {

    @Autowired
    private JacksonTester<UserDto> json;

    @Test
    void testJsonUserDto() throws IOException {
        var jsonContent = "{\"name\":\"Alex\", \"email\":\"1234@mail.ru\"}";
        var result = this.json.parse(jsonContent).getObject();

        assertThat(result.getName(), equalTo("Alex"));
        assertThat(result.getEmail(), equalTo("1234@mail.ru"));
    }
}
