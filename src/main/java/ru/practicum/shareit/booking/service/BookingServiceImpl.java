package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingJsonDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.enums.BookingState;
import ru.practicum.shareit.util.enums.BookingStatus;
import ru.practicum.shareit.util.exeption.NotFoundException;
import ru.practicum.shareit.util.exeption.NotFoundExceptionEntity;
import ru.practicum.shareit.util.mapper.BookingMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.practicum.shareit.util.mapper.BookingMapper.toBooking;
import static ru.practicum.shareit.util.mapper.BookingMapper.toBookingDto;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Override
    public BookingDto create(BookingJsonDto bookingJsonDto, Long userId) {
        if (bookingJsonDto.getEnd().isBefore(bookingJsonDto.getStart()) ||
                bookingJsonDto.getEnd().equals(bookingJsonDto.getStart()))
            throw new NotFoundException("Дата окончания бронирования не может быть позже даты старта или равна ей.");
        final Long itemId = bookingJsonDto.getItemId();
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundExceptionEntity("Пользователь с идентификатором : " + userId + " не найден."));
        final Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundExceptionEntity("Item с идентификатором : " + itemId + " не найден."));
        if (!item.getIsAvailable())
            throw new NotFoundException("Item не доступен для бронирования.");
        final Long id = item.getOwner().getId();
        if (Objects.equals(id, userId))
            throw new NotFoundExceptionEntity("Бронирование своего item запрещено.");
        final Booking booking = toBooking(bookingJsonDto, item, user);
        booking.setStatus(BookingStatus.WAITING);
        return toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto update(Long bookingId, Long userId, String approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundExceptionEntity("Booking с идентификатором : " + bookingId + " не найден."));
        final Long id = booking.getItem().getOwner().getId();
        if (!Objects.equals(id, userId))
            throw new NotFoundExceptionEntity("Пользователь с идентификатором : " + userId + " не может обновить статус item.");
        if (!booking.getStatus().equals(BookingStatus.WAITING))
            throw new NotFoundException("Статус booker должен быть WAITING.");
        if (approved.equals("true"))
            booking.setStatus(BookingStatus.APPROVED);
        if (approved.equals("false"))
            booking.setStatus(BookingStatus.REJECTED);
        return toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto findById(Long userId, Long bookingId) {
        return toBookingDto(bookingRepository.findById(bookingId)
                .filter(b -> Objects.equals(b.getBooker().getId(), userId) || Objects.equals(b.getItem().getOwner().getId(), userId))
                .orElseThrow(() -> new NotFoundExceptionEntity("Booking с идентификатором : " + bookingId + " не найден.")));
    }

    @Override
    public List<BookingDto> findAllByBooker(Long userId, String state) {
        final BookingState bookingState = BookingState.from(state)
                .orElseThrow(() -> new NotFoundException("Unknown state: " + state));
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundExceptionEntity("Пользователь с идентификатором : " + userId + " не найден."));
        final LocalDateTime date = LocalDateTime.now();
        final Sort sort = Sort.by("start").descending();
        switch (bookingState) {
            case ALL:
                return bookingRepository.findAllByBooker_Id(userId, sort)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case CURRENT:
                return bookingRepository.findByBooker_IdAndStartIsBeforeAndEndIsAfter(userId, date, date, sort)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case PAST:
                return bookingRepository.findByBooker_IdAndEndIsBefore(userId, date, sort)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case FUTURE:
                return bookingRepository.findByBooker_IdAndStartIsAfter(userId, date, sort)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case WAITING:
                return bookingRepository.findByBooker_IdAndStartIsAfterAndStatusIs(userId, date, sort, BookingStatus.WAITING)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case REJECTED:
                return bookingRepository.findByBooker_IdAndStartIsAfterAndStatusIs(userId, date, sort, BookingStatus.REJECTED)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            default:
                return null;
        }
    }

    @Override
    public List<BookingDto> findAllByOwner(Long userId, String state) {
        final BookingState bookingState = BookingState.from(state)
                .orElseThrow(() -> new NotFoundException("Unknown state: " + state));
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundExceptionEntity("Пользователь с идентификатором : " + userId + " не найден."));
        final List<Long> itemIdList = itemRepository.findAllByOwnerId(userId)
                .stream()
                .map(Item::getId)
                .collect(Collectors.toList());
        final LocalDateTime date = LocalDateTime.now();
        final Sort sort = Sort.by("start").descending();
        switch (bookingState) {
            case ALL:
                return bookingRepository.findAllByItem_IdIn(itemIdList, sort)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case CURRENT:
                return bookingRepository.findByItem_IdInAndStartIsBeforeAndEndIsAfter(itemIdList, date, date, sort)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case PAST:
                return bookingRepository.findByItem_IdInAndEndIsBefore(itemIdList, date, sort)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case FUTURE:
                return bookingRepository.findByItem_IdInAndStartIsAfter(itemIdList, date, sort)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case WAITING:
                return bookingRepository.findByItem_IdInAndStartIsAfterAndStatusIs(itemIdList, date, sort, BookingStatus.WAITING)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case REJECTED:
                return bookingRepository.findByItem_IdInAndStartIsAfterAndStatusIs(itemIdList, date, sort, BookingStatus.REJECTED)
                        .stream()
                        .map(BookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            default:
                return null;
        }
    }
}
