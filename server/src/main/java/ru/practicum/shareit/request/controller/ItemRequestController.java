package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

import static ru.practicum.shareit.util.Util.*;

@RestController
@Slf4j
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto create(@RequestHeader(USER_ID) Long userId,
                                 @RequestBody ItemRequestDto itemRequestDto) {
        final ItemRequestDto itemRequestDtoNew = itemRequestService.create(userId, itemRequestDto);
        log.debug("Создан запрос на бронирование вещи с описание: {}", itemRequestDto.getDescription());
        return itemRequestDtoNew;
    }

    @GetMapping
    public List<ItemRequestDto> findAllByOwner(@RequestHeader(USER_ID) Long userId) {
        final List<ItemRequestDto> itemRequestDto = itemRequestService.findAllByOwner(userId);
        log.debug("Получен список всех запросов пользователя: {}", userId);
        return itemRequestDto;
    }

    @GetMapping("/all")
    public List<ItemRequestDto> findAll(@RequestHeader(USER_ID) Long userId,
                                        @RequestParam(value = FROM, defaultValue = "0") Integer from,
                                        @RequestParam(value = SIZE, defaultValue = "10") Integer size) {
        final Sort sort = Sort.by("created").descending();
        final PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size, sort);
        final List<ItemRequestDto> itemRequestDto = itemRequestService.findAll(userId, page);
        log.debug("Получен список всех запросов для пользователя: {}", userId);
        return itemRequestDto;
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto findByRequestId(@RequestHeader(USER_ID) Long userId,
                                          @PathVariable Long requestId) {
        final ItemRequestDto itemRequestDto = itemRequestService.findByRequestId(userId, requestId);
        log.debug("Получен запрос на бронирование: {}", requestId);
        return itemRequestDto;
    }
}
