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
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.exeption.NotFoundExceptionEntity;
import ru.practicum.shareit.booking.BookingMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static ru.practicum.shareit.booking.BookingMapper.toBooking;
import static ru.practicum.shareit.booking.BookingMapper.toBookingDto;

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
        return toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto update(Long bookingId, Long userId, boolean isApproved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundExceptionEntity("Booking с идентификатором : " + bookingId + " не найден."));
        final Long id = booking.getItem().getOwner().getId();
        if (!Objects.equals(id, userId))
            throw new NotFoundExceptionEntity("Пользователь с идентификатором : " + userId + " не может обновить статус item.");
        if (!booking.getStatus().equals(BookingStatus.WAITING))
            throw new NotFoundException("Статус booker должен быть WAITING.");
        if (isApproved)
            booking.setStatus(BookingStatus.APPROVED);
        else
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
        List<Booking> bookings;
        switch (bookingState) {
            case ALL:
                bookings = bookingRepository.findAllByBooker_Id(userId, sort);
                break;
            case CURRENT:
                bookings = bookingRepository.findByBooker_IdAndStartIsBeforeAndEndIsAfter(userId, date, date, sort);
                break;
            case PAST:
                bookings = bookingRepository.findByBooker_IdAndEndIsBefore(userId, date, sort);
                break;
            case FUTURE:
                bookings = bookingRepository.findByBooker_IdAndStartIsAfter(userId, date, sort);
                break;
            case WAITING:
                bookings = bookingRepository.findByBooker_IdAndStartIsAfterAndStatusIs(userId, date, sort, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findByBooker_IdAndStartIsAfterAndStatusIs(userId, date, sort, BookingStatus.REJECTED);
                break;
            default:
                return emptyList();
        }
        return bookings
                .stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
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
        List<Booking> bookings;
        switch (bookingState) {
            case ALL:
                bookings = bookingRepository.findAllByItem_IdIn(itemIdList, sort);
                break;
            case CURRENT:
                bookings = bookingRepository.findByItem_IdInAndStartIsBeforeAndEndIsAfter(itemIdList, date, date, sort);
                break;
            case PAST:
                bookings = bookingRepository.findByItem_IdInAndEndIsBefore(itemIdList, date, sort);
                break;
            case FUTURE:
                bookings = bookingRepository.findByItem_IdInAndStartIsAfter(itemIdList, date, sort);
                break;
            case WAITING:
                bookings = bookingRepository.findByItem_IdInAndStartIsAfterAndStatusIs(itemIdList, date, sort, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findByItem_IdInAndStartIsAfterAndStatusIs(itemIdList, date, sort, BookingStatus.REJECTED);
                break;
            default:
                return emptyList();
        }
        return bookings
                .stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }
}
