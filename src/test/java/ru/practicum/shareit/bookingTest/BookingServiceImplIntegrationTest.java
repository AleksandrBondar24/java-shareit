package ru.practicum.shareit.bookingTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceImplIntegrationTest {

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    private final BookingRepository bookingRepository;

    private BookingService bookingService;
    private User user;
    private User owner;
    private BookingRequestDto bookingRequestDto;

    @BeforeEach
    public void createEnvironment() {
        bookingService = new BookingServiceImpl(itemRepository, userRepository, bookingRepository);
        user = new User();
        user.setName("Серж");
        user.setEmail("12345@mail.ru");
        owner = new User();
        owner.setName("Женя");
        owner.setEmail("54321@mail.ru");
        Item item = new Item();
        item.setOwner(owner);
        item.setIsAvailable(true);
        item.setName("Дрель");
        item.setDescription("Бош");
        userRepository.save(user);
        userRepository.save(owner);
        itemRepository.save(item);
        bookingRequestDto = new BookingRequestDto(item.getId(), LocalDateTime.now(), LocalDateTime.now().plusDays(1));
    }

    @Test
    void saveBooking() {
        var result = bookingService.create(bookingRequestDto, user.getId());

        assertThat(result, notNullValue());
        assertThat(result.getItem().getId(), equalTo(bookingRequestDto.getItemId()));
    }

    @Test
    void updateBooking() {
        var isApproved = true;
        final var bookingId = bookingService.create(bookingRequestDto, user.getId()).getId();
        var result = bookingService.update(bookingId, owner.getId(), isApproved);

        assertThat(result, notNullValue());
        assertThat(result.getStatus(), equalTo(BookingStatus.APPROVED));
    }

    @Test
    void getBooking() {
        final Long bookingId = bookingService.create(bookingRequestDto, user.getId()).getId();
        var result = bookingService.findById(user.getId(), bookingId);

        assertThat(result, notNullValue());
        assertThat(result.getId(), equalTo(bookingId));
    }

    @Test
    void getAllBookingsByBooker() {
        bookingService.create(bookingRequestDto, user.getId());
        final var state = "ALL";
        var result = bookingService.findAllByBooker(user.getId(), state, PageRequest.of(0, 10));

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(1));
    }

    @Test
    void getAllBookingsByOwner() {
        bookingService.create(bookingRequestDto, user.getId());
        final var state = "ALL";
        var result = bookingService.findAllByOwner(owner.getId(), state, PageRequest.of(0, 10));

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(1));
    }
}
