package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.enums.BookingStatus;
import ru.practicum.shareit.util.exeption.NotFoundException;
import ru.practicum.shareit.util.exeption.NotFoundExceptionEntity;
import ru.practicum.shareit.util.mapper.CommentMapper;
import ru.practicum.shareit.util.mapper.ItemMapper;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.shareit.util.mapper.BookingMapper.toBookingItemDto;
import static ru.practicum.shareit.util.mapper.CommentMapper.toComment;
import static ru.practicum.shareit.util.mapper.CommentMapper.toCommentResponseDto;
import static ru.practicum.shareit.util.mapper.ItemMapper.*;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Transactional
    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        final Item item = toItem(itemDto);
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundExceptionEntity("Пользователь с идентификатором : " + userId + " не найден."));
        item.setOwner(user);
        return toItemDto(itemRepository.save(item));
    }

    @Transactional
    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        final Item itemUpdate = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundExceptionEntity("Item с идентификатором : " + itemId + " не найден."));
        if (!userId.equals(itemUpdate.getOwner().getId())) {
            throw new NotFoundExceptionEntity("Владелец item с идентификатором : " + userId + " указан не верно.");
        }
        if (itemDto.getName() != null)
            itemUpdate.setName(itemDto.getName());
        if (itemDto.getDescription() != null)
            itemUpdate.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null)
            itemUpdate.setIsAvailable(itemDto.getAvailable());
        final Item item = itemRepository.save(itemUpdate);
        return toItemDto(item);
    }

    @Override
    public ItemResponseDto findById(Long itemId, Long userId) {
        final Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundExceptionEntity("Item с идентификатором : " + itemId + " не найден."));
        final List<CommentResponseDto> comments = commentRepository.findAllByItemId(itemId)
                .stream()
                .map(CommentMapper::toCommentResponseDto)
                .collect(Collectors.toList());
        final ItemResponseDto itemResponseDto = toItemResponseDto(item);
        itemResponseDto.setComments(comments);
        final Long id = item.getOwner().getId();
        if (Objects.equals(userId, id)) {
            final List<Booking> bookingList = bookingRepository.findByItem_Id(itemId);
            return saveDateBookings(itemResponseDto, bookingList);
        }
        return itemResponseDto;
    }

    @Override
    public List<ItemResponseDto> findAll(Long userId) {
        final List<ItemResponseDto> itemsList = itemRepository.findAllByOwnerId(userId)
                .stream()
                .map(ItemMapper::toItemResponseDto)
                .collect(Collectors.toList());
        final List<Long> itemsId = itemsList
                .stream()
                .map(ItemResponseDto::getId)
                .collect(Collectors.toList());
        final List<Booking> bookingList = bookingRepository.findAllByItem_IdIn(itemsId);
        return itemsList
                .stream()
                .map(itemsDto -> saveDateBookings(itemsDto, bookingList))
                .collect(Collectors.toList());
    }

    private ItemResponseDto saveDateBookings(ItemResponseDto itemsDto, List<Booking> bookingList) {
        final LocalDateTime time = LocalDateTime.now();
        Optional<Booking> bookingLast = bookingList
                .stream()
                .filter(booking -> Objects.equals(booking.getItem().getId(), itemsDto.getId()))
                .sorted(Comparator.comparing(Booking::getEnd).reversed())
                .filter(booking -> booking.getStatus().equals(BookingStatus.APPROVED))
                .filter(booking -> booking.getStart().isBefore(time))
                .limit(1)
                .findAny();
        bookingLast.ifPresent(booking -> itemsDto.setLastBooking(toBookingItemDto(booking)));

        Optional<Booking> bookingNext = bookingList
                .stream()
                .filter(booking -> Objects.equals(booking.getItem().getId(), itemsDto.getId()))
                .sorted(Comparator.comparing(Booking::getStart))
                .filter(booking -> booking.getStatus().equals(BookingStatus.APPROVED))
                .filter(booking -> booking.getStart().isAfter(time))
                .limit(1)
                .findAny();
        bookingNext.ifPresent(booking -> itemsDto.setNextBooking(toBookingItemDto(booking)));
        return itemsDto;
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        return itemRepository.findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndIsAvailableIsTrue(text, text)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public CommentResponseDto createComment(Long userId, CommentDto commentDto, Long itemId) {
        final Comment comment = toComment(commentDto);
        final LocalDateTime time = LocalDateTime.now();
        final User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundExceptionEntity("Пользователь с идентификатором : " + userId + " не найден."));
        final Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundExceptionEntity("Item с идентификатором : " + itemId + " не найден."));
        List<Booking> bookings = bookingRepository.findByItem_IdAndEndIsBefore(itemId, time)
                .stream()
                .filter(booking -> Objects.equals(booking.getBooker().getId(), userId))
                .collect(Collectors.toList());
        if (bookings.isEmpty()) {
            throw new NotFoundException("Пользователь не может оставить отзыв этой вещи.");
        }
        comment.setCreated(time);
        comment.setAuthor(author);
        comment.setItem(item);
        commentRepository.save(comment);
        return toCommentResponseDto(comment);
    }
}
