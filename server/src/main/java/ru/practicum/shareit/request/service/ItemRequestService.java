package ru.practicum.shareit.request.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> findAllByOwner(Long userId);

    List<ItemRequestDto> findAll(Long userId, PageRequest page);

    ItemRequestDto findByRequestId(Long userId, Long requestId);

}
