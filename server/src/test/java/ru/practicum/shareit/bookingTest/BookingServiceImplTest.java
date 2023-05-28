package ru.practicum.shareit.bookingTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exeption.NotFoundExceptionEntity;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceImplTest {

    @Mock
    private final ItemRepository itemRepository;

    @Mock
    private final UserRepository userRepository;

    @Mock
    private final BookingRepository bookingRepository;

    private BookingService bookingService;
    private User user;

    @BeforeEach
    public void createEnvironment() {
        bookingService = new BookingServiceImpl(itemRepository, userRepository, bookingRepository);
        user = new User();
        user.setId(1L);
        user.setName("Серж");
        user.setEmail("12345@mail.ru");

        when(bookingRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
    }

    @Test
    void getAllBookingsByBooker() {
        when(bookingRepository.findAllByBooker_Id(any(), any()))
                .thenReturn(Page.empty());

        var result = bookingService.findAllByBooker(1L, "ALL", PageRequest.of(0, 10));

        assertThat(result, notNullValue());
        assertThat("isEmpty", result.isEmpty());
        verify(bookingRepository, times(1)).findAllByBooker_Id(any(), any());
        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    void getPastBookingsByBooker() {
        when(bookingRepository.findByBooker_IdAndEndIsBefore(any(), any(), any()))
                .thenReturn(Page.empty());

        var result = bookingService.findAllByBooker(1L, "PAST", PageRequest.of(0, 10));

        assertThat(result, notNullValue());
        assertThat("isEmpty", result.isEmpty());
        verify(bookingRepository, times(1)).findByBooker_IdAndEndIsBefore(any(), any(), any());
        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    void getFutureBookingsByBooker() {
        when(bookingRepository.findByBooker_IdAndStartIsAfter(any(), any(), any()))
                .thenReturn(Page.empty());

        var result = bookingService.findAllByBooker(1L, "FUTURE", PageRequest.of(0, 10));

        assertThat(result, notNullValue());
        assertThat("isEmpty", result.isEmpty());
        verify(bookingRepository, times(1)).findByBooker_IdAndStartIsAfter(any(), any(), any());
        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    void getCurrentBookingsByBooker() {
        when(bookingRepository.findByBooker_IdAndStartIsBeforeAndEndIsAfter(any(), any(), any(), any()))
                .thenReturn(Page.empty());

        var result = bookingService.findAllByBooker(1L, "CURRENT", PageRequest.of(0, 10));

        assertThat(result, notNullValue());
        assertThat("isEmpty", result.isEmpty());
        verify(bookingRepository, times(1)).findByBooker_IdAndStartIsBeforeAndEndIsAfter(any(), any(), any(), any());
        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    void getWaitingBookingsByBooker() {
        when(bookingRepository.findByBooker_IdAndStartIsAfterAndStatusIs(any(), any(), any(), any()))
                .thenReturn(Page.empty());

        var result = bookingService.findAllByBooker(1L, "WAITING", PageRequest.of(0, 10));

        assertThat(result, notNullValue());
        assertThat("isEmpty", result.isEmpty());
        verify(bookingRepository, times(1)).findByBooker_IdAndStartIsAfterAndStatusIs(any(), any(), any(), any());
        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    void getRejectedBookingsByBooker() {
        when(bookingRepository.findByBooker_IdAndStartIsAfterAndStatusIs(any(), any(), any(), any()))
                .thenReturn(Page.empty());

        var result = bookingService.findAllByBooker(1L, "REJECTED", PageRequest.of(0, 10));

        assertThat(result, notNullValue());
        assertThat("isEmpty", result.isEmpty());
        verify(bookingRepository, times(1)).findByBooker_IdAndStartIsAfterAndStatusIs(any(), any(), any(), any());
        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    void getAllOwnerBookings() {
        when(bookingRepository.findAllByItem_IdIn(any(), any()))
                .thenReturn(Page.empty());

        var result = bookingService.findAllByOwner(1L, "ALL", PageRequest.of(0, 10));

        assertThat(result, notNullValue());
        assertThat("isEmpty", result.isEmpty());
        verify(bookingRepository, times(1)).findAllByItem_IdIn(any(), any());
        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    void getPastOwnerBookings() {
        when(bookingRepository.findByItem_IdInAndEndIsBefore(any(), any(), any()))
                .thenReturn(Page.empty());

        var result = bookingService.findAllByOwner(1L, "PAST", PageRequest.of(0, 10));

        assertThat(result, notNullValue());
        assertThat("isEmpty", result.isEmpty());
        verify(bookingRepository, times(1)).findByItem_IdInAndEndIsBefore(any(), any(), any());
        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    void getFutureOwnerBookings() {
        when(bookingRepository.findByItem_IdInAndStartIsAfter(any(), any(), any()))
                .thenReturn(Page.empty());

        var result = bookingService.findAllByOwner(1L, "FUTURE", PageRequest.of(0, 10));

        assertThat(result, notNullValue());
        assertThat("isEmpty", result.isEmpty());
        verify(bookingRepository, times(1)).findByItem_IdInAndStartIsAfter(any(), any(), any());
        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    void getCurrentOwnerBookings() {
        when(bookingRepository.findByItem_IdInAndStartIsBeforeAndEndIsAfter(any(), any(), any(), any()))
                .thenReturn(Page.empty());

        var result = bookingService.findAllByOwner(1L, "CURRENT", PageRequest.of(0, 10));

        assertThat(result, notNullValue());
        assertThat("isEmpty", result.isEmpty());
        verify(bookingRepository, times(1)).findByItem_IdInAndStartIsBeforeAndEndIsAfter(any(), any(), any(), any());
        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    void getWaitingOwnerBookings() {
        when(bookingRepository.findByItem_IdInAndStartIsAfterAndStatusIs(any(), any(), any(), any()))
                .thenReturn(Page.empty());

        var result = bookingService.findAllByOwner(1L, "WAITING", PageRequest.of(0, 10));

        assertThat(result, notNullValue());
        assertThat("isEmpty", result.isEmpty());
        verify(bookingRepository, times(1)).findByItem_IdInAndStartIsAfterAndStatusIs(any(), any(), any(), any());
        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    void getRejectedOwnerBookings() {
        when(bookingRepository.findByItem_IdInAndStartIsAfterAndStatusIs(any(), any(), any(), any()))
                .thenReturn(Page.empty());

        var result = bookingService.findAllByOwner(1L, "REJECTED", PageRequest.of(0, 10));

        assertThat(result, notNullValue());
        assertThat("isEmpty", result.isEmpty());
        verify(bookingRepository, times(1)).findByItem_IdInAndStartIsAfterAndStatusIs(any(), any(), any(), any());
        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    void saveBooking() {
        var userId = 1L;
        var user = new User();
        user.setId(2L);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        var bookingRequestDto = new BookingRequestDto(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1));

        Item item = new Item();
        item.setId(3L);
        item.setOwner(user);
        item.setIsAvailable(true);

        when(itemRepository.findById(bookingRequestDto.getItemId()))
                .thenReturn(Optional.of(item));

        var result = bookingService.create(bookingRequestDto, userId);

        assertThat(result, notNullValue());
        verify(userRepository, times(1)).findById(userId);
        verify(itemRepository, times(1)).findById(1L);
    }

    @Test
    void getBooking() {
        var bookingId = 2L;

        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.empty());

        verify(userRepository, times(0)).findById(user.getId());
        verify(bookingRepository, times(0)).findById(bookingId);
        Assertions.assertThrows(NotFoundExceptionEntity.class, () -> bookingService.findById(user.getId(), bookingId));
    }

    @Test
    void setBookingStateApproved() {
        var bookingId = 2L;
        var approved = true;
        Item item = new Item();
        item.setId(3L);
        item.setOwner(user);
        item.setIsAvailable(true);
        var booking = new Booking();
        booking.setId(3L);
        booking.setStatus(BookingStatus.WAITING);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setBooker(user);
        booking.setItem(item);

        when(bookingRepository.findByIdAndItemOwnerId(bookingId, user.getId()))
                .thenReturn(Optional.of(booking));

        var result = bookingService.update(bookingId, user.getId(), approved);

        assertThat(result, notNullValue());
        assertThat(result.getStatus(), equalTo(BookingStatus.APPROVED));
        verify(userRepository, times(1)).findById(user.getId());
        verify(bookingRepository, times(1)).findByIdAndItemOwnerId(bookingId, user.getId());
    }

    @Test
    void setBookingStateRejected() {
        var bookingId = 2L;
        var approved = false;
        Item item = new Item();
        item.setId(3L);
        item.setOwner(user);
        item.setIsAvailable(true);
        var booking = new Booking();
        booking.setId(3L);
        booking.setStatus(BookingStatus.WAITING);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setBooker(user);
        booking.setItem(item);

        when(bookingRepository.findByIdAndItemOwnerId(bookingId, user.getId()))
                .thenReturn(Optional.of(booking));

        var result = bookingService.update(bookingId, user.getId(), approved);

        assertThat(result, notNullValue());
        assertThat(result.getStatus(), equalTo(BookingStatus.REJECTED));
        verify(userRepository, times(1)).findById(user.getId());
        verify(bookingRepository, times(1)).findByIdAndItemOwnerId(bookingId, user.getId());
    }
}
