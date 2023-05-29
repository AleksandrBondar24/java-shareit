package ru.practicum.shareit.itemTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.item.dto.CommentDto;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JsonTest
public class CommentDtoJsonTest {
    @Autowired
    private JacksonTester<CommentDto> json;

    @Test
    void testJsonCommentDto() throws IOException {
        var jsonContent = "{\"text\":\"Новая дрель\"}";

        var result = this.json.parse(jsonContent).getObject();

        assertThat(result.getText()).isEqualTo("Новая дрель");
    }
}
