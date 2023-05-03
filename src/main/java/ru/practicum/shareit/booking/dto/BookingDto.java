package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.util.enums.BookingStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class BookingDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Item item;
    private Booker booker;
    private BookingStatus status;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Booker {
        private Long id;
        private String name;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Item {
        private Long id;
        private String name;
    }
}
