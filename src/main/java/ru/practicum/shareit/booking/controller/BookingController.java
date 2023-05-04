package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;

import ru.practicum.shareit.booking.dto.BookingJsonDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@Slf4j
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                             @Valid @RequestBody BookingJsonDto bookingJsonDto) {
        BookingDto bookingDto = bookingService.create(bookingJsonDto, userId);
        log.debug("Создан booking на item с идентификатором : {}", bookingJsonDto.getItemId());
        return bookingDto;
    }

    @PatchMapping("/{bookingId}")
    public BookingDto update(@RequestHeader("X-Sharer-User-Id") Long userId,
                             @PathVariable Long bookingId,
                             @RequestParam(value = "approved") String approved) {
        boolean isApproved = approved.equals("true");
        BookingDto bookingDto = bookingService.update(bookingId, userId, isApproved);
        log.debug("Обновлен статус booking на item с идентификатором : {}", bookingId);
        return bookingDto;
    }

    @GetMapping("/{bookingId}")
    public BookingDto findById(@RequestHeader("X-Sharer-User-Id") Long userId,
                               @PathVariable Long bookingId) {
        BookingDto bookingDto = bookingService.findById(userId, bookingId);
        log.debug("Получен booking с идентификатором : {}", bookingId);
        return bookingDto;
    }

    @GetMapping
    public List<BookingDto> findAllByBooker(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @RequestParam(value = "state", defaultValue = "ALL", required = false) String state) {
        List<BookingDto> bookingDtoList = bookingService.findAllByBooker(userId, state);
        log.debug("Получен список bookings пользователя с id : {}", userId);
        return bookingDtoList;
    }

    @GetMapping("/owner")
    public List<BookingDto> findAllByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @RequestParam(value = "state", defaultValue = "ALL", required = false) String state) {
        List<BookingDto> bookingDtoList = bookingService.findAllByOwner(userId, state);
        log.debug("Получен список bookings для вещей пользователя с id : {}", userId);
        return bookingDtoList;
    }
}
