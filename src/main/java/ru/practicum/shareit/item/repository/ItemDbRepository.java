package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ItemDbRepository implements ItemRepository {

    private final Map<Long, Item> itemStorage = new HashMap<>();

    @Override
    public Item create(Long userId, Item item) {
        item.setOwner(userId);
        itemStorage.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item item) {
        itemStorage.put(item.getId(), item);
        return itemStorage.get(item.getId());
    }

    @Override
    public Item findById(Long itemId) {
        return itemStorage.get(itemId);
    }

    @Override
    public List<Item> findAll(Long userId) {
        return itemStorage.values()
                .stream()
                .filter(item -> item.getOwner().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> searchItems(String text) {
        return itemStorage.values()
                .stream()
                .filter(i -> i.getName().toLowerCase().contains(text.toLowerCase()) ||
                        i.getDescription().toLowerCase().contains(text.toLowerCase()) && i.getIsAvailable())
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> getItemStorage() {
        return new ArrayList<>(itemStorage.values());
    }
}

