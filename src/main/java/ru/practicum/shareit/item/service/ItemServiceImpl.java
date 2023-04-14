package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.exeption.NotFoundException;
import ru.practicum.shareit.util.exeption.NotFoundExceptionEntity;
import ru.practicum.shareit.util.mapper.ItemMapper;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.util.mapper.ItemMapper.toItem;
import static ru.practicum.shareit.util.mapper.ItemMapper.toItemDto;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private Long id = 1L;

    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        userService.findById(userId);
        itemDto.setId(id);
        Item createItem = itemRepository.create(userId, toItem(itemDto));
        id++;
        return toItemDto(createItem);
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        Item itemUpdate = itemRepository.getItemStorage()
                .stream()
                .filter(item -> item.getOwner().equals(userId))
                .findAny()
                .orElseThrow(() -> new NotFoundExceptionEntity("Владелец item с идентификатором : " + userId + " указан не верно."));
        if (itemDto.getName() != null)
            itemUpdate.setName(itemDto.getName());
        if (itemDto.getDescription() != null)
            itemUpdate.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null)
            itemUpdate.setIsAvailable(itemDto.getAvailable());
        Item returnsItem = itemRepository.update(itemUpdate);
        return toItemDto(returnsItem);
    }

    @Override
    public ItemDto findById(Long itemId) {
        Item item = itemRepository.findById(itemId);
        if (item == null)
            throw new NotFoundException("Item с идентификатором : " + itemId + " не найден.");
        return toItemDto(item);
    }

    @Override
    public List<ItemDto> findAll(Long userId) {
        return itemRepository.findAll(userId)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        return itemRepository.searchItems(text)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
