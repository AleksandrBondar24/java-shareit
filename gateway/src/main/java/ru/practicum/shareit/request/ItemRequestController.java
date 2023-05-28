package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.util.Util.*;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@RequestHeader(USER_ID) Long userId,
                                                    @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.debug("Получен запрос на бронирование вещи с описание: {}", itemRequestDto.getDescription());
        return itemRequestClient.createItemRequest(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getItemRequestsByOwner(@RequestHeader(USER_ID) Long userId) {
        log.debug("Получение списка всех запросов пользователя: {}", userId);
        return itemRequestClient.getItemRequestsByOwner(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getItemRequests(@RequestHeader(USER_ID) Long userId,
                                                  @PositiveOrZero @RequestParam(value = FROM, defaultValue = "0") Integer from,
                                                  @Positive @RequestParam(value = SIZE, defaultValue = "10") Integer size) {
        log.debug("Получение списка всех запросов для пользователя: {}", userId);
        return itemRequestClient.getItemRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequest(@RequestHeader(USER_ID) Long userId,
                                                 @PathVariable Long requestId) {
        log.debug("Получение запроса на бронирование: {}", requestId);
        return itemRequestClient.getItemRequest(userId, requestId);
    }
}
