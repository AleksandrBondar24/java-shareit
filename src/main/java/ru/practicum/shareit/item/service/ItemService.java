package ru.practicum.shareit.item.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;

import java.util.List;

public interface ItemService {
    ItemDto create(Long userId, ItemDto itemDto);

    ItemDto update(Long userId, Long itemId, ItemDto itemDto);

    ItemResponseDto findById(Long itemId, Long userId);

    List<ItemResponseDto> findAll(Long userId,PageRequest page);

    List<ItemDto> searchItems(Long userId, String text, PageRequest page);

    CommentResponseDto createComment(Long userId, CommentDto commentDto, Long itemId);
}
