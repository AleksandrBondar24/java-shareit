package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

import static ru.practicum.shareit.util.Util.*;

@RestController
@RequestMapping(path = "/bookings")
@Slf4j
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private final Sort sort = Sort.by("start").descending();

    @PostMapping
    public BookingDto create(@RequestHeader(USER_ID) Long userId,
                             @RequestBody BookingRequestDto bookingRequestDto) {
        BookingDto bookingDto = bookingService.create(bookingRequestDto, userId);
        log.debug("Создан booking на item с идентификатором : {}", bookingRequestDto.getItemId());
        return bookingDto;
    }

    @PatchMapping("/{bookingId}")
    public BookingDto update(@RequestHeader(USER_ID) Long userId,
                             @PathVariable Long bookingId,
                             @RequestParam(value = "approved") String approved) {
        boolean isApproved = approved.equals("true");
        BookingDto bookingDto = bookingService.update(bookingId, userId, isApproved);
        log.debug("Обновлен статус booking на item с идентификатором : {}", bookingId);
        return bookingDto;
    }

    @GetMapping("/{bookingId}")
    public BookingDto findById(@RequestHeader(USER_ID) Long userId,
                               @PathVariable Long bookingId) {
        BookingDto bookingDto = bookingService.findById(userId, bookingId);
        log.debug("Получен booking с идентификатором : {}", bookingId);
        return bookingDto;
    }

    @GetMapping
    public List<BookingDto> findAllByBooker(@RequestHeader(USER_ID) Long userId,
                                            @RequestParam(value = STATE, defaultValue = "ALL", required = false) String state,
                                            @RequestParam(value = FROM, defaultValue = "0") Integer from,
                                            @RequestParam(value = SIZE, defaultValue = "10") Integer size) {
        final PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size, sort);
        List<BookingDto> bookingDtoList = bookingService.findAllByBooker(userId, state, page);
        log.debug("Получен список bookings пользователя с id : {}", userId);
        return bookingDtoList;
    }

    @GetMapping("/owner")
    public List<BookingDto> findAllByOwner(@RequestHeader(USER_ID) Long userId,
                                           @RequestParam(value = STATE, defaultValue = "ALL", required = false) String state,
                                           @RequestParam(value = FROM, defaultValue = "0") Integer from,
                                           @RequestParam(value = SIZE, defaultValue = "10") Integer size) {
        final PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size, sort);
        List<BookingDto> bookingDtoList = bookingService.findAllByOwner(userId, state, page);
        log.debug("Получен список bookings для вещей пользователя с id : {}", userId);
        return bookingDtoList;
    }
}
