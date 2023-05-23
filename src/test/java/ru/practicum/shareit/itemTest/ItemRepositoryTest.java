package ru.practicum.shareit.itemTest;

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
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@DataJpaTest
public class ItemRepositoryTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    private User owner;
    private ItemRequest itemRequest;
    final PageRequest page = PageRequest.of(0, 10);

    @BeforeEach
    public void createEnvironment() {
        User user = new User();
        user.setName("Серж");
        user.setEmail("12345.@mail.ru");
        owner = new User();
        owner.setName("Женя");
        owner.setEmail("54321@mail.ru");
        userRepository.save(user);
        userRepository.save(owner);
        itemRequest = new ItemRequest();
        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setDescription("Новая дрель");
        itemRequestRepository.save(itemRequest);
        Item item = new Item();
        item.setName("Дрель");
        item.setDescription("Новая");
        item.setIsAvailable(Boolean.TRUE);
        item.setOwner(owner);
        item.setRequest(itemRequest);
        itemRepository.save(item);
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(3));
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.WAITING);
        bookingRepository.save(booking);
        Comment comment = new Comment();
        comment.setText("Норм");
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());
        comment.setItem(item);
        commentRepository.save(comment);
    }

    @Test
    void contextLoads() {
        Assertions.assertNotNull(em);
    }

    @Test
    void verifyFindAllByOwnerId() {
        var ownerId = owner.getId();
        var result = itemRepository.findAllByOwnerId(ownerId, page)
                .stream()
                .collect(Collectors.toList());

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(1));
    }

    @Test
    void verifyFindAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndIsAvailableIsTrue() {
        var text = "Дрель";
        var result = itemRepository.findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndIsAvailableIsTrue(
                        text, text, page)
                .stream()
                .collect(Collectors.toList());

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(1));
    }

    @Test
    void verifyFindAllByRequestIdIn() {
        var result = itemRepository.findAllByRequestIdIn(List.of(itemRequest.getId()));

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(1));
    }

    @Test
    void verifyFindAllByRequestId() {
        var result = itemRepository.findAllByRequestId(itemRequest.getId());

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(1));
    }

    @Test
    void verifyFindAllByOwnerIdList() {
        var ownerId = owner.getId();
        var result = itemRepository.findAllByOwnerId(ownerId);

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(1));
    }
}
