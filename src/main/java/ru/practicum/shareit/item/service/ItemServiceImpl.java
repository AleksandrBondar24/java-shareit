package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.mapper.ItemMapper;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.util.mapper.ItemMapper.toItem;
import static ru.practicum.shareit.util.mapper.ItemMapper.toItemDto;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        userRepository.findById(userId);
        Item createItem = itemRepository.create(userId, toItem(itemDto));
        return toItemDto(createItem);
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        Item item = itemRepository.update(userId, itemId, toItem(itemDto));
        return toItemDto(item);
    }

    @Override
    public ItemDto findById(Long itemId) {
        Item item = itemRepository.findById(itemId);
        return toItemDto(item);
    }

    @Override
    public List<ItemDto> findAll(Long userId) {
        return itemRepository.findAll(userId).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        return itemRepository.searchItems(text).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }
}
