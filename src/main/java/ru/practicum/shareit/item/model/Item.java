package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.request.model.ItemRequest;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Item {
    private Long id;
    private String name;
    private String description;
    private Boolean isAvailable;
    private Long owner;
    private ItemRequest request;
}