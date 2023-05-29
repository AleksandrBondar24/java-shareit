package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Page<Item> findAllByOwnerId(Long ownerId, PageRequest page);

    List<Item> findAllByOwnerId(Long ownerId);

    Page<Item> findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndIsAvailableIsTrue(String textName, String textDescription, PageRequest page);

    List<Item> findAllByRequestIdIn(List<Long> listRequestIds);

    List<Item> findAllByRequestId(Long requestId);
}
