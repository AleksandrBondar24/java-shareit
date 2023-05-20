package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.exeption.NotFoundException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/items")
@Slf4j
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @Valid @RequestBody ItemDto itemDto) {
        validate(itemDto);
        final ItemDto createdItem = itemService.create(userId, itemDto);
        log.debug("Добавлен item с идентификатором : {}", createdItem.getId());
        return createdItem;
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @PathVariable Long itemId,
                          @RequestBody ItemDto itemDto) {
        final ItemDto updateItem = itemService.update(userId, itemId, itemDto);
        log.debug("Item с идентификатором : " + itemId + " обновлен.");
        return updateItem;
    }

    @GetMapping("/{itemId}")
    public ItemResponseDto findById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @PathVariable Long itemId) {
        final ItemResponseDto itemsDto = itemService.findById(itemId, userId);
        log.debug("Получен item с идентификатором : {}", itemId);
        return itemsDto;
    }

    @GetMapping
    public List<ItemResponseDto> findAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
                                         @Positive @RequestParam(value = "size", defaultValue = "10") Integer size) {
        final PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        final List<ItemResponseDto> findAllItem = itemService.findAll(userId, page);
        log.debug("Получен список всех item пользователя : {}", userId);
        return findAllItem;
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @RequestParam(value = "text") String text,
                                     @Valid @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
                                     @Positive @RequestParam(value = "size", defaultValue = "10") Integer size) {
        if (text == null || text.isBlank())
            return Collections.emptyList();
        final PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        final List<ItemDto> items = itemService.searchItems(userId, text, page);
        log.debug("Получен список вещей по ключевому слову : {}", text);
        return items;
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponseDto createComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @Valid @RequestBody CommentDto commentDto,
                                            @PathVariable Long itemId) {
        final CommentResponseDto commentNew = itemService.createComment(userId, commentDto, itemId);
        log.debug("Добавлен новый отзыв для вещи : {}", itemId);
        return commentNew;
    }

    private void validate(ItemDto itemDto) {
        if (itemDto.getName() == null ||
                itemDto.getName().isBlank() ||
                itemDto.getDescription() == null ||
                itemDto.getDescription().isBlank() ||
                itemDto.getAvailable() == null) {
            throw new NotFoundException("Имя или описание не указаны.");
        }
    }
}
