package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.exeption.NotFoundException;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/items")
@Slf4j
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @RequestBody ItemDto itemDto) {
        validate(itemDto);
        ItemDto createdItem = itemService.create(userId, itemDto);
        log.debug("Добавлен item с идентификатором : {}", createdItem.getId());
        return createdItem;
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @PathVariable Long itemId,
                          @RequestBody ItemDto itemDto) {
        ItemDto updateItem = itemService.update(userId, itemId, itemDto);
        log.debug("Item с идентификатором : " + itemId + " обновлен.");
        return updateItem;
    }

    @GetMapping("/{itemId}")
    public ItemResponseDto findById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @PathVariable Long itemId) {
        ItemResponseDto itemsDto = itemService.findById(itemId, userId);
        log.debug("Получен item с идентификатором : {}", itemId);
        return itemsDto;
    }

    @GetMapping
    public List<ItemResponseDto> findAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        List<ItemResponseDto> findAllItem = itemService.findAll(userId);
        log.debug("Получен список всех item пользователя : {}", userId);
        return findAllItem;
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam(value = "text") String text) {
        if (text == null || text.isBlank())
            return Collections.emptyList();
        List<ItemDto> items = itemService.searchItems(text);
        log.debug("Получен список вещей по ключевому слову : {}", text);
        return items;
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponseDto createComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @Valid @RequestBody CommentDto commentDto,
                                            @PathVariable Long itemId) {
        CommentResponseDto commentNew = itemService.createComment(userId, commentDto, itemId);
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
