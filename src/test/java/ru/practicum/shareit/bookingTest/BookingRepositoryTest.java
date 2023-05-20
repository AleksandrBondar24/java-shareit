package ru.practicum.shareit.bookingTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@DataJpaTest
public class BookingRepositoryTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    private Booking booking;
    private Item item;
    private User user;
    final private PageRequest page = PageRequest.of(0, 10);

    @BeforeEach
    public void createEnvironment() {
        user = new User();
        user.setName("Серж");
        user.setEmail("12345.@mail.ru");
        userRepository.save(user);
        item = new Item();
        item.setName("Дрель");
        item.setDescription("Новая");
        item.setIsAvailable(Boolean.TRUE);
        item.setOwner(user);
        itemRepository.save(item);
        booking = new Booking();
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(3));
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.WAITING);
        bookingRepository.save(booking);
    }

    @Test
    void contextLoads() {
        Assertions.assertNotNull(em);
    }

    @Test
    void verifyFindByBooker_IdAndEndIsBefore() {
        var userId = user.getId();
        var date = LocalDateTime.now();
        var result = bookingRepository.findByBooker_IdAndEndIsBefore(userId, date, page)
                .stream()
                .collect(Collectors.toList());

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(0));
    }

    @Test
    void verifyFindAllByBooker_Id() {
        var userId = user.getId();
        var result = bookingRepository.findAllByBooker_Id(userId, page)
                .stream()
                .collect(Collectors.toList());

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(1));
    }

    @Test
    void verifyFindByBooker_IdAndStartIsBeforeAndEndIsAfter() {
        var userId = user.getId();
        var date = LocalDateTime.now();
        var result = bookingRepository.findByBooker_IdAndStartIsAfter(userId, date, page)
                .stream()
                .collect(Collectors.toList());

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(1));
    }

    @Test
    void verifyFindAllByItem_IdIn() {
        var itemId = item.getId();
        var result = bookingRepository.findAllByItem_IdIn(List.of(itemId), page)
                .stream()
                .collect(Collectors.toList());

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(1));
    }

    @Test
    void verifyFindByItem_IdInAndStartIsBeforeAndEndIsAfter() {
        var itemId = item.getId();
        var date = LocalDateTime.now();
        var result = bookingRepository.findByItem_IdInAndStartIsBeforeAndEndIsAfter(List.of(itemId), date, date, page)
                .stream()
                .collect(Collectors.toList());

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(0));
    }

    @Test
    void verifyFindByItem_IdInAndEndIsBefore() {
        var itemId = item.getId();
        var date = LocalDateTime.now();
        var result = bookingRepository.findByItem_IdInAndEndIsBefore(List.of(itemId), date, page)
                .stream()
                .collect(Collectors.toList());

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(0));
    }

    @Test
    void verifyFindByItem_IdInAndStartIsAfterAndStatusIs() {
        var itemId = item.getId();
        var date = LocalDateTime.now();
        var status = BookingStatus.WAITING;
        var result = bookingRepository.findByItem_IdInAndStartIsAfterAndStatusIs(List.of(itemId), date, status, page)
                .stream()
                .collect(Collectors.toList());

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(1));
    }

    @Test
    void verifyFindByBooker_IdAndStartIsAfterAndStatusIs() {
        var userId = user.getId();
        var date = LocalDateTime.now();
        var status = BookingStatus.WAITING;
        var result = bookingRepository.findByBooker_IdAndStartIsAfterAndStatusIs(userId, date, status, page)
                .stream()
                .collect(Collectors.toList());

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(1));
    }

    @Test
    void verifyFindByItem_IdInAndStartIsAfter() {
        var itemId = item.getId();
        var status = BookingStatus.WAITING;
        var result = bookingRepository.findAllByItem_IdInAndStatusIs(List.of(itemId), status);

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(1));
    }

    @Test
    void verifyFindByItem_IdAndStatusIs() {
        var status = BookingStatus.WAITING;
        var itemId = item.getId();
        var result = bookingRepository.findByItem_IdAndStatusIs(itemId, status);

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(1));
    }

    @Test
    void verifyFindByItem_IdAndEndIsBefore() {
        var itemId = item.getId();
        var date = LocalDateTime.now();
        var result = bookingRepository.findByItem_IdAndEndIsBefore(itemId, date);

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(0));
    }

    @Test
    void verifyFindByIdAndItemOwnerId() {
        var userId = user.getId();
        var bookingId = booking.getId();
        var result = bookingRepository.findByIdAndItemOwnerId(bookingId, userId);

        assertThat(result, notNullValue());
        assertThat(result.get().getStatus(), equalTo(BookingStatus.WAITING));
    }
}
