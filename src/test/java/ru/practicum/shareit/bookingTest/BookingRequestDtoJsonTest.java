package ru.practicum.shareit.bookingTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


@JsonTest
public class BookingRequestDtoJsonTest {
    @Autowired
    private JacksonTester<BookingRequestDto> json;

    @Test
    void testJsonBookingRequestDto() throws IOException {
        var jsonContent = "{\"itemId\":\"1\", \"start\":\"2023-05-22T12:00:01\", \"end\":\"2023-05-23T13:00:01\"}";

        var result = this.json.parse(jsonContent).getObject();

        assertThat(result.getItemId()).isEqualTo(1L);
        assertThat(result.getStart()).isEqualTo(LocalDateTime.of(2023, 5, 22, 12, 0, 1));
        assertThat(result.getEnd()).isEqualTo(LocalDateTime.of(2023, 5, 23, 13, 0, 1));
    }
}


