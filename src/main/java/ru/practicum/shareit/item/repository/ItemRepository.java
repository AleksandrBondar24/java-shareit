package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Item create(Long userId, Item item);

    Item update(Long userId, Long itemId, Item item);

    Item findById(Long itemId);

    List<Item> findAll(Long userId);

    List<Item> searchItems(String text);
}
