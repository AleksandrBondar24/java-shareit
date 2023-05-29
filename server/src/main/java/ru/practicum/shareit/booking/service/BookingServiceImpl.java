package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
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
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional
    public BookingDto create(BookingRequestDto bookingRequestDto, Long userId) {
        if (bookingRequestDto.getEnd().isBefore(bookingRequestDto.getStart()) ||
                bookingRequestDto.getEnd().equals(bookingRequestDto.getStart()))
            throw new NotFoundException("Дата окончания бронирования не может быть позже даты старта или равна ей.");
        final Long itemId = bookingRequestDto.getItemId();
        final User user = chekUser(userId);
        final Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundExceptionEntity("Item с идентификатором : " + itemId + " не найден."));
        if (!item.getIsAvailable())
            throw new NotFoundException("Item не доступен для бронирования.");
        final Long id = item.getOwner().getId();
        if (Objects.equals(id, userId))
            throw new NotFoundExceptionEntity("Бронирование своего item запрещено.");
        final Booking booking = toBooking(bookingRequestDto, item, user);
        return toBookingDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDto update(Long bookingId, Long userId, boolean isApproved) {
        final User user = chekUser(userId);
        Booking booking = bookingRepository.findByIdAndItemOwnerId(bookingId, userId)
                .orElseThrow(() -> new NotFoundExceptionEntity("Booking с идентификатором : " + bookingId + " не найден."));
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
        final User user = chekUser(userId);
        return toBookingDto(bookingRepository.findById(bookingId)
                .filter(b -> Objects.equals(b.getBooker().getId(), userId) || Objects.equals(b.getItem().getOwner().getId(), userId))
                .orElseThrow(() -> new NotFoundExceptionEntity("Booking с идентификатором : " + bookingId + " не найден.")));
    }

    @Override
    public List<BookingDto> findAllByBooker(Long userId, String state, PageRequest page) {
        final BookingState bookingState = BookingState.valueOf(state);
        final User user = chekUser(userId);
        final LocalDateTime date = LocalDateTime.now();
        Page<Booking> bookings;
        switch (bookingState) {
            case ALL:
                bookings = bookingRepository.findAllByBooker_Id(userId, page);
                break;
            case CURRENT:
                bookings = bookingRepository.findByBooker_IdAndStartIsBeforeAndEndIsAfter(userId, date, date, page);
                break;
            case PAST:
                bookings = bookingRepository.findByBooker_IdAndEndIsBefore(userId, date, page);
                break;
            case FUTURE:
                bookings = bookingRepository.findByBooker_IdAndStartIsAfter(userId, date, page);
                break;
            case WAITING:
                bookings = bookingRepository.findByBooker_IdAndStartIsAfterAndStatusIs(userId, date, BookingStatus.WAITING, page);
                break;
            case REJECTED:
                bookings = bookingRepository.findByBooker_IdAndStartIsAfterAndStatusIs(userId, date, BookingStatus.REJECTED, page);
                break;
            default:
                return emptyList();
        }
        return bookings
                .map(BookingMapper::toBookingDto)
                .getContent();

    }

    @Override
    public List<BookingDto> findAllByOwner(Long userId, String state, PageRequest page) {
        final BookingState bookingState = BookingState.valueOf(state);
        final User user = chekUser(userId);
        final List<Long> itemIdList = itemRepository.findAllByOwnerId(userId)
                .stream()
                .map(Item::getId)
                .collect(Collectors.toList());
        final LocalDateTime date = LocalDateTime.now();
        Page<Booking> bookings;
        switch (bookingState) {
            case ALL:
                bookings = bookingRepository.findAllByItem_IdIn(itemIdList, page);
                break;
            case CURRENT:
                bookings = bookingRepository.findByItem_IdInAndStartIsBeforeAndEndIsAfter(itemIdList, date, date, page);
                break;
            case PAST:
                bookings = bookingRepository.findByItem_IdInAndEndIsBefore(itemIdList, date, page);
                break;
            case FUTURE:
                bookings = bookingRepository.findByItem_IdInAndStartIsAfter(itemIdList, date, page);
                break;
            case WAITING:
                bookings = bookingRepository.findByItem_IdInAndStartIsAfterAndStatusIs(itemIdList, date, BookingStatus.WAITING, page);
                break;
            case REJECTED:
                bookings = bookingRepository.findByItem_IdInAndStartIsAfterAndStatusIs(itemIdList, date, BookingStatus.REJECTED, page);
                break;
            default:
                return emptyList();
        }
        return bookings
                .map(BookingMapper::toBookingDto)
                .getContent();
    }

    private User chekUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundExceptionEntity("Пользователь с идентификатором : " + userId + " не найден."));
    }
}
