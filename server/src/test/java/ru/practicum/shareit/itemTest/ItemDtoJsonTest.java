package ru.practicum.shareit.itemTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.item.dto.ItemDto;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JsonTest
public class ItemDtoJsonTest {
    @Autowired
    private JacksonTester<ItemDto> json;

    @Test
    void testJsonItemDto() throws IOException {
        var jsonContent = "{\"name\":\"Дрель\", \"description\":\"Новая\", \"available\":\"true\"}";

        var result = this.json.parse(jsonContent).getObject();

        assertThat(result.getName()).isEqualTo("Дрель");
        assertThat(result.getDescription()).isEqualTo("Новая");
        assertThat(result.getAvailable()).isEqualTo(true);
    }
}
