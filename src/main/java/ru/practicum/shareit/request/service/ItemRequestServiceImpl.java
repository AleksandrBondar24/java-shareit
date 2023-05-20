package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exeption.NotFoundExceptionEntity;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.request.ItemRequestMapper.toItemRequest;
import static ru.practicum.shareit.request.ItemRequestMapper.toItemRequestDto;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto) {
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundExceptionEntity("Пользователь с идентификатором : " + userId + " не найден."));
        final ItemRequest itemRequest = toItemRequest(itemRequestDto, user);
        return toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> findAllByOwner(Long userId) {
        if (!checkUser(userId))
            throw new NotFoundExceptionEntity("Пользователь с идентификатором : " + userId + " не найден.");
        final Sort sort = Sort.by("created").descending();
        final List<ItemRequestDto> itemRequestDto = itemRequestRepository.findAllByRequestor_Id(userId, sort)
                .stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
        return addResponse(itemRequestDto);
    }

    @Override
    public List<ItemRequestDto> findAll(Long userId, PageRequest page) {
        if (!checkUser(userId))
            throw new NotFoundExceptionEntity("Пользователь с идентификатором : " + userId + " не найден.");
        final List<ItemRequestDto> itemRequestDto = itemRequestRepository.findAllByRequestor_IdNot(userId, page)
                .map(ItemRequestMapper::toItemRequestDto)
                .getContent();
        return addResponse(itemRequestDto);
    }

    @Override
    public ItemRequestDto findByRequestId(Long userId, Long requestId) {
        if (!checkUser(userId))
            throw new NotFoundExceptionEntity("Пользователь с идентификатором : " + userId + " не найден.");
        final ItemRequestDto itemRequestDto = toItemRequestDto(itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundExceptionEntity("Запрос на бронирование вещи не найден.")));
        final List<ItemDto> itemDtoList = itemRepository.findAllByRequestId(requestId)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        itemRequestDto.setItems(itemDtoList);
        return itemRequestDto;
    }

    private Boolean checkUser(Long userId) {
        return userRepository.existsById(userId);
    }

    private List<ItemRequestDto> addResponse(List<ItemRequestDto> itemRequestDto) {
        final List<Long> listRequestIds = itemRequestDto
                .stream()
                .map(ItemRequestDto::getId)
                .collect(Collectors.toList());
        final List<ItemDto> itemDtoList = itemRepository.findAllByRequestIdIn(listRequestIds)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        itemRequestDto
                .forEach(r -> r.setItems(itemDtoList
                        .stream()
                        .filter(itemDto -> itemDto.getRequestId().equals(r.getId()))
                        .collect(Collectors.toList())));
        return itemRequestDto;
    }
}
