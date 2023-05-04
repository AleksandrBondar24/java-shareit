package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingJsonDto;

import java.util.List;

public interface BookingService {
    BookingDto create(BookingJsonDto bookingJsonDto, Long userId);

    BookingDto update(Long bookingId, Long userId, boolean isApproved);

    BookingDto findById(Long userId, Long bookingId);

    List<BookingDto> findAllByBooker(Long userId, String state);

    List<BookingDto> findAllByOwner(Long userId, String state);
}
