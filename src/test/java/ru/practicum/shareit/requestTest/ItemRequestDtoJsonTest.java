package ru.practicum.shareit.requestTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JsonTest
public class ItemRequestDtoJsonTest {
    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    void testJsonItemRequestDto() throws IOException {
        var jsonContent = "{\"description\":\"Новая дрель\"}";
        var result = this.json.parse(jsonContent).getObject();

        assertThat(result.getDescription()).isEqualTo("Новая дрель");
    }
}
