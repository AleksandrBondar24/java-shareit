package ru.practicum.shareit.booking;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.util.Util.*;


@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;
    private final static String URL = "?state={state}&from={from}&size={size}";
    private final static String URL_NEW = "/owner?state={state}&from={from}&size={size}";

    @GetMapping
    public ResponseEntity<Object> getBookings(@RequestHeader(USER_ID) long userId,
                                              @RequestParam(name = STATE, defaultValue = "all") String stateParam,
                                              @PositiveOrZero @RequestParam(name = FROM, defaultValue = "0") Integer from,
                                              @Positive @RequestParam(name = SIZE, defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getBookings(URL, userId, state, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> bookItem(@RequestHeader(USER_ID) long userId,
                                           @RequestBody @Valid BookItemRequestDto requestDto) {
        log.info("Creating booking {}, userId={}", requestDto, userId);
        return bookingClient.bookItem(userId, requestDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader(USER_ID) long userId,
                                             @PathVariable Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsByOwner(@RequestHeader(USER_ID) long ownerId,
                                                     @RequestParam(name = STATE, defaultValue = "all") String stateParam,
                                                     @PositiveOrZero @RequestParam(name = FROM, defaultValue = "0") Integer from,
                                                     @Positive @RequestParam(name = SIZE, defaultValue = "10") Integer size) {
        BookingState state = ru.practicum.shareit.booking.dto.BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, ownerId, from, size);
        return bookingClient.getBookings(URL_NEW, ownerId, state, from, size);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBooking(@RequestHeader(USER_ID) Long userId,
                                                @PathVariable Long bookingId,
                                                @RequestParam(value = "approved") String approved) {
        boolean isApproved = approved.equals("true");
        log.info("Получен запрос на обновление бронирования с id: {}", bookingId);
        return bookingClient.updateBooking(userId, bookingId, isApproved);
    }
}
