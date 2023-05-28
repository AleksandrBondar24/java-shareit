package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static ru.practicum.shareit.util.Util.*;

@RestController
@RequestMapping("/items")
@Slf4j
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto create(@RequestHeader(USER_ID) Long userId,
                          @RequestBody ItemDto itemDto) {
        final ItemDto createdItem = itemService.create(userId, itemDto);
        log.debug("Добавлен item с идентификатором : {}", createdItem.getId());
        return createdItem;
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(USER_ID) Long userId,
                          @PathVariable Long itemId,
                          @RequestBody ItemDto itemDto) {
        final ItemDto updateItem = itemService.update(userId, itemId, itemDto);
        log.debug("Item с идентификатором : " + itemId + " обновлен.");
        return updateItem;
    }

    @GetMapping("/{itemId}")
    public ItemResponseDto findById(@RequestHeader(USER_ID) Long userId,
                                    @PathVariable Long itemId) {
        final ItemResponseDto itemsDto = itemService.findById(itemId, userId);
        log.debug("Получен item с идентификатором : {}", itemId);
        return itemsDto;
    }

    @GetMapping
    public List<ItemResponseDto> findAll(@RequestHeader(USER_ID) Long userId,
                                         @RequestParam(value = FROM, defaultValue = "0") Integer from,
                                         @RequestParam(value = SIZE, defaultValue = "10") Integer size) {
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        final PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size, sort);
        final List<ItemResponseDto> findAllItem = itemService.findAll(userId, page);
        log.debug("Получен список всех item пользователя : {}", userId);
        return findAllItem;
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestHeader(USER_ID) Long userId,
                                     @RequestParam(value = "text") String text,
                                     @RequestParam(value = FROM, defaultValue = "0") Integer from,
                                     @RequestParam(value = SIZE, defaultValue = "10") Integer size) {
        final PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        final List<ItemDto> items = itemService.searchItems(userId, text, page);
        log.debug("Получен список вещей по ключевому слову : {}", text);
        return items;
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponseDto createComment(@RequestHeader(USER_ID) Long userId,
                                            @RequestBody CommentDto commentDto,
                                            @PathVariable Long itemId) {
        final CommentResponseDto commentNew = itemService.createComment(userId, commentDto, itemId);
        log.debug("Добавлен новый отзыв для вещи : {}", itemId);
        return commentNew;
    }
}
