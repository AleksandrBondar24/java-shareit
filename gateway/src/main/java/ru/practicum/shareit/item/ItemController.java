package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.util.Util.*;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(USER_ID) long userId,
                                             @Valid @RequestBody ItemDto itemDto) {
        log.info("Получен запрос на создание item");
        return itemClient.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(USER_ID) Long userId,
                                             @PathVariable Long itemId,
                                             @RequestBody ItemDto itemDto) {
        log.info("Получен запрос на обновление Item с идентификатором : " + itemId);
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader(USER_ID) Long userId,
                                          @PathVariable Long itemId) {
        log.info("Запрос на получение item с идентификатором : {}", itemId);
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getItems(@RequestHeader(USER_ID) Long userId,
                                           @PositiveOrZero @RequestParam(value = FROM, defaultValue = "0") Integer from,
                                           @Positive @RequestParam(value = SIZE, defaultValue = "10") Integer size) {
        log.info("Запрос на получение списка всех item пользователя : {}", userId);
        return itemClient.getItems(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestHeader(USER_ID) Long userId,
                                              @RequestParam(value = "text") String text,
                                              @Valid @PositiveOrZero @RequestParam(value = FROM, defaultValue = "0") Integer from,
                                              @Positive @RequestParam(value = SIZE, defaultValue = "10") Integer size) {
        log.info("Запрос на получение списка вещей по ключевому слову : {}", text);
        return itemClient.searchItems(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader(USER_ID) Long userId,
                                                @Valid @RequestBody CommentDto commentDto,
                                                @PathVariable Long itemId) {
        log.info("Получен запрос на добавление отзыва для вещи : {}", itemId);
        return itemClient.createComment(userId, commentDto, itemId);
    }
}
