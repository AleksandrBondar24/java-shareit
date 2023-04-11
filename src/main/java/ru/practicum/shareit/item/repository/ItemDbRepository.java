package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.util.exeption.NotFoundException;
import ru.practicum.shareit.util.exeption.NotFoundExceptionEntity;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ItemDbRepository implements ItemRepository {

    Map<Long, List<Item>> itemRepository = new HashMap<>();
    private Long id = 1L;

    @Override
    public Item create(Long userId, Item item) {
        List<Item> listItems = itemRepository.getOrDefault(userId, new ArrayList<>());
        item.setId(id);
        item.setOwner(userId);
        listItems.add(item);
        itemRepository.put(userId, listItems);
        id++;
        return item;
    }

    @Override
    public Item update(Long userId, Long itemId, Item item) {
        if (itemRepository.get(userId) == null) {
            throw new NotFoundExceptionEntity("Владелец item с идентификатором : " + userId + " указан не верно.");
        }
        Item updateItem = findById(itemId);
        if (item.getName() != null) {
            updateItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            updateItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            updateItem.setAvailable(item.getAvailable());
        }
        return updateItem;
    }

    @Override
    public Item findById(Long itemId) {
        return itemRepository.values()
                .stream()
                .flatMap(Collection::stream)
                .filter(i -> i.getId().equals(itemId))
                .findAny()
                .orElseThrow(() -> new NotFoundException("Item с идентификатором : " + itemId + " не найден."));
    }

    @Override
    public List<Item> findAll(Long userId) {
        return new ArrayList<>(itemRepository.get(userId));
    }

    @Override
    public List<Item> searchItems(String text) {
        return itemRepository.values()
                .stream()
                .flatMap(Collection::stream)
                .filter(i -> i.getName().toLowerCase().contains(text.toLowerCase()) ||
                        i.getDescription().toLowerCase().contains(text.toLowerCase()) && i.getAvailable())
                .collect(Collectors.toList());
    }
}

