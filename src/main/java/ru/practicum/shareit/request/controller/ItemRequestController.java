package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@Slf4j
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private final ItemRequestService itemRequestDtoService;

    @PostMapping
    public ItemRequestDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @Valid @RequestBody ItemRequestDto itemRequestDto) {
        final ItemRequestDto itemRequestDtoNew = itemRequestDtoService.create(userId, itemRequestDto);
        log.debug("Создан запрос на бронирование вещи с описание: {}", itemRequestDto.getDescription());
        return itemRequestDtoNew;
    }

    @GetMapping
    public List<ItemRequestDto> findAllByOwner(@RequestHeader("X-Sharer-User-Id") Long userId) {
        final List<ItemRequestDto> itemRequestDto = itemRequestDtoService.findAllByOwner(userId);
        log.debug("Получен список всех запросов пользователя: {}", userId);
        return itemRequestDto;
    }

    @GetMapping("/all")
    public List<ItemRequestDto> findAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
                                        @Positive @RequestParam(value = "size", defaultValue = "10") Integer size) {
        final Sort sort = Sort.by("created").descending();
        final PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size, sort);
        final List<ItemRequestDto> itemRequestDto = itemRequestDtoService.findAll(userId, page);
        log.debug("Получен список всех запросов для пользователя: {}", userId);
        return itemRequestDto;
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto findByRequestId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @PathVariable Long requestId) {
        final ItemRequestDto itemRequestDto = itemRequestDtoService.findByRequestId(userId, requestId);
        log.debug("Получен запрос на бронирование: {}", requestId);
        return itemRequestDto;
    }
}
