package ru.practicum.shareit.booking.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(force = true)
@EqualsAndHashCode
@ToString
public class BookingRequestDto {
    private Long itemId;

    private LocalDateTime start;

    private LocalDateTime end;
}
